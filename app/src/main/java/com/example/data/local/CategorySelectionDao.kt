package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategorySelectionDao {

    @Query("SELECT * FROM category_selections ORDER BY categoryTitle ASC")
    fun getAllCategoriesFlow(): Flow<List<CategorySelectionEntity>>

    @Query("SELECT * FROM category_selections")
    suspend fun getAllCategoriesList(): List<CategorySelectionEntity>

    @Query("SELECT * FROM selected_products ORDER BY categoryId, productName ASC")
    fun getAllProductsFlow(): Flow<List<SelectedProductEntity>>

    @Query("SELECT * FROM selected_products WHERE categoryId = :categoryId ORDER BY productName ASC")
    fun getProductsByCategoryFlow(categoryId: String): Flow<List<SelectedProductEntity>>

    @Query("SELECT * FROM selected_products WHERE categoryId = :categoryId")
    suspend fun getProductsByCategoryList(categoryId: String): List<SelectedProductEntity>

    @Query("SELECT * FROM selected_products WHERE isSelected = 1")
    fun getSelectedProductsFlow(): Flow<List<SelectedProductEntity>>

    @Query("SELECT * FROM selected_products WHERE isSelected = 1")
    suspend fun getSelectedProductsList(): List<SelectedProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCategory(category: CategorySelectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCategories(categories: List<CategorySelectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProduct(product: SelectedProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProducts(products: List<SelectedProductEntity>)

    @Query("UPDATE category_selections SET isSelected = :isSelected, lastUpdated = :timestamp WHERE categoryId = :categoryId")
    suspend fun updateCategorySelected(categoryId: String, isSelected: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE selected_products SET isSelected = :isSelected, lastUpdated = :timestamp WHERE categoryId = :categoryId")
    suspend fun updateAllProductsInCategory(categoryId: String, isSelected: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE selected_products SET isSelected = :isSelected, lastUpdated = :timestamp WHERE productId = :productId")
    suspend fun updateProductSelected(productId: String, isSelected: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE category_selections SET isSelected = :isSelected, lastUpdated = :timestamp")
    suspend fun updateAllCategories(isSelected: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE selected_products SET isSelected = :isSelected, lastUpdated = :timestamp")
    suspend fun updateAllProducts(isSelected: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE category_selections SET selectedProductCount = :selectedCount, isSelected = :isSelected, lastUpdated = :timestamp WHERE categoryId = :categoryId")
    suspend fun updateCategoryCount(categoryId: String, selectedCount: Int, isSelected: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM category_selections WHERE isSelected = 1")
    fun getSelectedCategoriesCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM selected_products WHERE isSelected = 1")
    fun getSelectedProductsCountFlow(): Flow<Int>
}
