package com.example.data.local

import com.example.data.model.ChemicalCategory
import com.example.data.model.ChemicalCatalog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for managing product and category selections persisted in the Room database.
 */
class CategorySelectionRepository(
    private val dao: CategorySelectionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val allCategories: Flow<List<CategorySelectionEntity>> = dao.getAllCategoriesFlow()
    val allProducts: Flow<List<SelectedProductEntity>> = dao.getAllProductsFlow()
    val selectedProducts: Flow<List<SelectedProductEntity>> = dao.getSelectedProductsFlow()
    val selectedCategoriesCount: Flow<Int> = dao.getSelectedCategoriesCountFlow()
    val selectedProductsCount: Flow<Int> = dao.getSelectedProductsCountFlow()

    fun getProductsByCategory(categoryId: String): Flow<List<SelectedProductEntity>> {
        return dao.getProductsByCategoryFlow(categoryId)
    }

    /**
     * Seeds initial catalog entries if the database tables are empty.
     */
    suspend fun seedInitialDataIfEmpty() = withContext(ioDispatcher) {
        val existingCategories = dao.getAllCategoriesList()
        val categoriesToSeed = ChemicalCategory.values().filter { it != ChemicalCategory.ALL }

        if (existingCategories.isEmpty()) {
            val initialCategories = categoriesToSeed.map { category ->
                val totalCount = ChemicalCatalog.products.count { it.category == category }
                CategorySelectionEntity(
                    categoryId = category.id,
                    categoryTitle = category.title,
                    isSelected = false,
                    selectedProductCount = 0,
                    totalProductCount = totalCount,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            dao.insertOrUpdateCategories(initialCategories)

            val initialProducts = ChemicalCatalog.products.map { product ->
                SelectedProductEntity(
                    productId = product.id,
                    productName = product.name,
                    categoryId = product.category.id,
                    categoryTitle = product.category.title,
                    chemicalFormula = product.formula,
                    purity = product.purity,
                    packaging = product.packagingOptions.firstOrNull() ?: "",
                    isSelected = false,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            dao.insertOrUpdateProducts(initialProducts)
        }
    }

    /**
     * Toggles category selection in Room DB. When a category is selected/deselected,
     * all products within that category are also updated in the Room DB.
     */
    suspend fun setCategorySelected(categoryId: String, isSelected: Boolean) = withContext(ioDispatcher) {
        val timestamp = System.currentTimeMillis()
        dao.updateCategorySelected(categoryId, isSelected, timestamp)
        dao.updateAllProductsInCategory(categoryId, isSelected, timestamp)

        val products = dao.getProductsByCategoryList(categoryId)
        val selectedCount = if (isSelected) products.size else 0
        dao.updateCategoryCount(categoryId, selectedCount, isSelected, timestamp)
    }

    /**
     * Toggles an individual product's selection state in Room DB.
     * Recalculates the parent category's selection state and count in Room DB.
     */
    suspend fun setProductSelected(productId: String, isSelected: Boolean) = withContext(ioDispatcher) {
        val timestamp = System.currentTimeMillis()
        dao.updateProductSelected(productId, isSelected, timestamp)

        // Find parent category to sync category-level status
        val allProducts = dao.getAllCategoriesList() // Check all categories
        for (cat in allProducts) {
            val catProducts = dao.getProductsByCategoryList(cat.categoryId)
            if (catProducts.any { it.productId == productId }) {
                val selectedCount = catProducts.count { if (it.productId == productId) isSelected else it.isSelected }
                val isCategoryFullySelected = selectedCount == catProducts.size && catProducts.isNotEmpty()
                dao.updateCategoryCount(cat.categoryId, selectedCount, isCategoryFullySelected, timestamp)
                break
            }
        }
    }

    /**
     * Selects or deselects all categories and products in Room DB.
     */
    suspend fun setAllSelected(isSelected: Boolean) = withContext(ioDispatcher) {
        val timestamp = System.currentTimeMillis()
        dao.updateAllCategories(isSelected, timestamp)
        dao.updateAllProducts(isSelected, timestamp)

        val categories = dao.getAllCategoriesList()
        for (cat in categories) {
            val products = dao.getProductsByCategoryList(cat.categoryId)
            val count = if (isSelected) products.size else 0
            dao.updateCategoryCount(cat.categoryId, count, isSelected, timestamp)
        }
    }

    /**
     * Returns all currently selected products for direct conversion to pending inquiries.
     */
    suspend fun getSelectedProductsList(): List<SelectedProductEntity> = withContext(ioDispatcher) {
        dao.getSelectedProductsList()
    }
}
