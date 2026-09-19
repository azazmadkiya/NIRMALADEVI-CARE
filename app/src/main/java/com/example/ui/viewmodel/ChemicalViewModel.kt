package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.PendingInquiryEntity
import com.example.data.local.PendingInquiryRepository
import com.example.data.local.CategorySelectionEntity
import com.example.data.local.SelectedProductEntity
import com.example.data.local.CategorySelectionRepository
import com.example.data.model.ChemicalCatalog
import com.example.data.model.ChemicalCategory
import com.example.data.model.ChemicalProduct
import com.example.data.model.CustomerInquiry
import com.example.data.model.QuoteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DilutionState(
    val stockConcentration: String = "98",      // e.g. 98% Sulphuric or 33% HCl
    val targetConcentration: String = "10",     // e.g. 10%
    val targetVolume: String = "100",           // e.g. 100 Liters
    val calculatedStockRequired: Double? = 10.20,
    val calculatedWaterRequired: Double? = 89.80,
    val calculationError: String? = null
)

data class MassVolumeState(
    val volumeLiters: String = "1000",
    val specificGravity: String = "1.84",       // Default to Sulphuric Acid
    val calculatedMassKg: Double? = 1840.0,
    val calculatedMassMT: Double? = 1.84
)

data class ChemicalUiState(
    val selectedCategory: ChemicalCategory = ChemicalCategory.ALL,
    val searchQuery: String = "",
    val filteredProducts: List<ChemicalProduct> = ChemicalCatalog.products,
    val selectedProductForDetail: ChemicalProduct? = null,
    val isProductDetailOpen: Boolean = false,
    val quoteItems: List<QuoteItem> = emptyList(),
    val customerName: String = "",
    val companyName: String = "",
    val customerPhone: String = "",
    val deliveryLocation: String = "",
    val isUrgent: Boolean = false,
    val notes: String = "",
    val favorites: Set<String> = emptySet(),
    val dilutionState: DilutionState = DilutionState(),
    val massVolumeState: MassVolumeState = MassVolumeState(),
    val snackbarMessage: String? = null,
    val pendingInquiries: List<PendingInquiryEntity> = emptyList(),
    val isDatabaseInitialized: Boolean = false,
    val categorySelections: List<CategorySelectionEntity> = emptyList(),
    val selectedProductsInDb: List<SelectedProductEntity> = emptyList(),
    val selectedCategoryCount: Int = 0,
    val selectedProductCount: Int = 0,
    val expandedCategoryIds: Set<String> = emptySet(),
    val selectionSearchQuery: String = "",
    val selectionFilterOnlySelected: Boolean = false
)

class ChemicalViewModel(
    initialRepository: PendingInquiryRepository? = null,
    initialCategoryRepository: CategorySelectionRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChemicalUiState())
    val uiState: StateFlow<ChemicalUiState> = _uiState.asStateFlow()

    private var repository: PendingInquiryRepository? = initialRepository
    private var categoryRepository: CategorySelectionRepository? = initialCategoryRepository

    init {
        // Initialize default category selections for instant display
        val defaultCategories = ChemicalCategory.values().filter { it != ChemicalCategory.ALL }.map { cat ->
            val totalCount = ChemicalCatalog.products.count { it.category == cat }
            CategorySelectionEntity(
                categoryId = cat.id,
                categoryTitle = cat.title,
                isSelected = false,
                selectedProductCount = 0,
                totalProductCount = totalCount
            )
        }
        val defaultProducts = ChemicalCatalog.products.map { prod ->
            SelectedProductEntity(
                productId = prod.id,
                productName = prod.name,
                categoryId = prod.category.id,
                categoryTitle = prod.category.title,
                chemicalFormula = prod.formula,
                purity = prod.purity,
                packaging = prod.packagingOptions.firstOrNull() ?: "",
                isSelected = false
            )
        }
        _uiState.update { state ->
            state.copy(
                categorySelections = defaultCategories,
                selectedProductsInDb = defaultProducts,
                expandedCategoryIds = defaultCategories.map { it.categoryId }.toSet()
            )
        }

        // If repositories were provided at construction, observe them immediately
        initialRepository?.let { repo ->
            observeRepository(repo)
        }
        initialCategoryRepository?.let { catRepo ->
            observeCategoryRepository(catRepo)
        }
    }

    /**
     * Initializes the Room Database repositories if not already attached.
     */
    fun initDatabase(context: Context) {
        val db = AppDatabase.getDatabase(context)
        if (repository == null) {
            val repo = PendingInquiryRepository(db.pendingInquiryDao())
            repository = repo
            observeRepository(repo)
        }
        if (categoryRepository == null) {
            val catRepo = CategorySelectionRepository(db.categorySelectionDao())
            categoryRepository = catRepo
            viewModelScope.launch {
                catRepo.seedInitialDataIfEmpty()
                observeCategoryRepository(catRepo)
            }
        }
        _uiState.update { it.copy(isDatabaseInitialized = true) }
    }

    private fun observeCategoryRepository(catRepo: CategorySelectionRepository) {
        viewModelScope.launch {
            catRepo.allCategories.collect { categories ->
                if (categories.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            categorySelections = categories,
                            selectedCategoryCount = categories.count { it.isSelected }
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            catRepo.allProducts.collect { products ->
                if (products.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            selectedProductsInDb = products,
                            selectedProductCount = products.count { it.isSelected }
                        )
                    }
                }
            }
        }
    }

    private fun observeRepository(repo: PendingInquiryRepository) {
        viewModelScope.launch {
            repo.allPendingInquiries.collect { inquiries ->
                _uiState.update { it.copy(pendingInquiries = inquiries, isDatabaseInitialized = true) }
            }
        }
    }

    /**
     * Inserts a new pending inquiry item into the Room database.
     */
    fun addPendingInquiry(
        userName: String,
        companyName: String,
        quantity: Int,
        product: ChemicalProduct,
        unit: String = "Carboy (30-50 kg)",
        grade: String = "Industrial Grade",
        notes: String = ""
    ) {
        val entity = PendingInquiryEntity(
            userName = userName.trim(),
            companyName = companyName.trim(),
            quantity = quantity,
            productId = product.id,
            productName = product.name,
            productCategory = product.category.title,
            chemicalFormula = product.formula,
            grade = grade,
            packagingUnit = unit,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )

        // Optimistic synchronous UI update
        _uiState.update { state ->
            val newId = (state.pendingInquiries.maxOfOrNull { it.id } ?: 0L) + 1L
            state.copy(
                pendingInquiries = listOf(entity.copy(id = newId)) + state.pendingInquiries,
                snackbarMessage = "Saved inquiry for ${product.name} to Room DB"
            )
        }

        // Persist to Room database in background
        viewModelScope.launch {
            repository?.insert(entity)
        }
    }

    /**
     * Updates the quantity of an existing pending inquiry record in Room DB.
     */
    fun updatePendingQuantity(id: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            deletePendingInquiry(id)
            return
        }

        // Optimistic synchronous UI update
        _uiState.update { state ->
            state.copy(
                pendingInquiries = state.pendingInquiries.map {
                    if (it.id == id) it.copy(quantity = newQuantity) else it
                }
            )
        }

        // Persist to Room database in background
        viewModelScope.launch {
            repository?.updateQuantity(id, newQuantity)
        }
    }

    /**
     * Deletes a pending inquiry item from the Room DB.
     */
    fun deletePendingInquiry(id: Long) {
        // Optimistic synchronous UI update
        _uiState.update { state ->
            state.copy(
                pendingInquiries = state.pendingInquiries.filterNot { it.id == id },
                snackbarMessage = "Inquiry removed from Room DB"
            )
        }

        // Persist to Room database in background
        viewModelScope.launch {
            repository?.deleteById(id)
        }
    }

    /**
     * Clears all pending inquiries from Room DB.
     */
    fun clearAllPendingInquiries() {
        // Optimistic synchronous UI update
        _uiState.update {
            it.copy(
                pendingInquiries = emptyList(),
                snackbarMessage = "All pending inquiries cleared from Room DB"
            )
        }

        // Persist to Room database in background
        viewModelScope.launch {
            repository?.clearAll()
        }
    }

    fun selectCategory(category: ChemicalCategory) {
        _uiState.update { state ->
            state.copy(selectedCategory = category)
        }
        applyFilters()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(searchQuery = query)
        }
        applyFilters()
    }

    private fun applyFilters() {
        val category = _uiState.value.selectedCategory
        val query = _uiState.value.searchQuery.trim().lowercase()
        val normalizedQuery = query.replace("ph", "f")

        val filtered = ChemicalCatalog.products.filter { product ->
            val matchesCategory = (category == ChemicalCategory.ALL) || (product.category == category)
            val nameNormalized = product.name.lowercase().replace("ph", "f")
            val matchesQuery = query.isEmpty() ||
                    product.name.lowercase().contains(query) ||
                    nameNormalized.contains(normalizedQuery) ||
                    product.category.title.lowercase().contains(query) ||
                    product.category.id.lowercase().contains(query) ||
                    product.formula.lowercase().contains(query) ||
                    product.tag.lowercase().contains(query) ||
                    product.purity.lowercase().contains(query) ||
                    product.applications.any { it.lowercase().contains(query) || it.lowercase().replace("ph", "f").contains(normalizedQuery) }
            matchesCategory && matchesQuery
        }

        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    fun openProductDetail(product: ChemicalProduct) {
        _uiState.update {
            it.copy(
                selectedProductForDetail = product,
                isProductDetailOpen = true
            )
        }
    }

    fun closeProductDetail() {
        _uiState.update {
            it.copy(isProductDetailOpen = false)
        }
    }

    fun toggleFavorite(productId: String) {
        _uiState.update { state ->
            val newFavorites = state.favorites.toMutableSet()
            if (newFavorites.contains(productId)) {
                newFavorites.remove(productId)
            } else {
                newFavorites.add(productId)
            }
            state.copy(favorites = newFavorites)
        }
    }

    fun addToQuote(
        product: ChemicalProduct,
        grade: String,
        quantity: Int,
        unit: String,
        isWholesale: Boolean,
        remarks: String
    ) {
        val newItem = QuoteItem(
            product = product,
            selectedGrade = grade,
            quantity = quantity,
            unit = unit,
            isWholesale = isWholesale,
            remarks = remarks
        )
        _uiState.update { state ->
            state.copy(
                quoteItems = state.quoteItems + newItem,
                snackbarMessage = "Added ${product.name} to Inquiry list"
            )
        }
    }

    fun updateQuoteItemQuantity(itemId: String, newQty: Int) {
        if (newQty <= 0) {
            removeQuoteItem(itemId)
            return
        }
        _uiState.update { state ->
            state.copy(
                quoteItems = state.quoteItems.map {
                    if (it.id == itemId) it.copy(quantity = newQty) else it
                }
            )
        }
    }

    fun removeQuoteItem(itemId: String) {
        _uiState.update { state ->
            state.copy(
                quoteItems = state.quoteItems.filterNot { it.id == itemId },
                snackbarMessage = "Item removed from quote"
            )
        }
    }

    fun clearQuote() {
        _uiState.update { state ->
            state.copy(
                quoteItems = emptyList(),
                snackbarMessage = "Inquiry list cleared"
            )
        }
    }

    fun updateCustomerInfo(
        name: String,
        company: String,
        phone: String,
        location: String,
        urgent: Boolean,
        notes: String
    ) {
        _uiState.update { state ->
            state.copy(
                customerName = name,
                companyName = company,
                customerPhone = phone,
                deliveryLocation = location,
                isUrgent = urgent,
                notes = notes
            )
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // Dilution Calculator: C1 * V1 = C2 * V2  => V1 = (C2 * V2) / C1
    fun calculateDilution(stockConcStr: String, targetConcStr: String, targetVolStr: String) {
        val c1 = stockConcStr.toDoubleOrNull()
        val c2 = targetConcStr.toDoubleOrNull()
        val v2 = targetVolStr.toDoubleOrNull()

        if (c1 == null || c2 == null || v2 == null || c1 <= 0.0 || c2 <= 0.0 || v2 <= 0.0) {
            _uiState.update {
                it.copy(
                    dilutionState = DilutionState(
                        stockConcentration = stockConcStr,
                        targetConcentration = targetConcStr,
                        targetVolume = targetVolStr,
                        calculatedStockRequired = null,
                        calculatedWaterRequired = null,
                        calculationError = "Please enter valid positive numbers"
                    )
                )
            }
            return
        }

        if (c2 > c1) {
            _uiState.update {
                it.copy(
                    dilutionState = DilutionState(
                        stockConcentration = stockConcStr,
                        targetConcentration = targetConcStr,
                        targetVolume = targetVolStr,
                        calculatedStockRequired = null,
                        calculatedWaterRequired = null,
                        calculationError = "Target concentration cannot be higher than stock ($c1%)"
                    )
                )
            }
            return
        }

        val v1 = (c2 * v2) / c1
        val waterReq = (v2 - v1).coerceAtLeast(0.0)

        _uiState.update {
            it.copy(
                dilutionState = DilutionState(
                    stockConcentration = stockConcStr,
                    targetConcentration = targetConcStr,
                    targetVolume = targetVolStr,
                    calculatedStockRequired = Math.round(v1 * 100.0) / 100.0,
                    calculatedWaterRequired = Math.round(waterReq * 100.0) / 100.0,
                    calculationError = null
                )
            )
        }
    }

    // Mass - Volume Converter: Mass (kg) = Volume (L) * Specific Gravity
    fun calculateMassVolume(volumeStr: String, spGrStr: String) {
        val vol = volumeStr.toDoubleOrNull()
        val spGr = spGrStr.toDoubleOrNull()

        if (vol != null && spGr != null && vol >= 0 && spGr >= 0) {
            val massKg = vol * spGr
            val massMt = massKg / 1000.0
            _uiState.update {
                it.copy(
                    massVolumeState = MassVolumeState(
                        volumeLiters = volumeStr,
                        specificGravity = spGrStr,
                        calculatedMassKg = Math.round(massKg * 100.0) / 100.0,
                        calculatedMassMT = Math.round(massMt * 1000.0) / 1000.0
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    massVolumeState = MassVolumeState(
                        volumeLiters = volumeStr,
                        specificGravity = spGrStr,
                        calculatedMassKg = null,
                        calculatedMassMT = null
                    )
                )
            }
        }
    }

    /**
     * Toggles category selection in the Room database.
     * When selected/deselected, all chemical products under this category are updated in Room DB.
     */
    fun toggleCategorySelectionInDb(categoryId: String, isSelected: Boolean) {
        val categoryTitle = _uiState.value.categorySelections.find { it.categoryId == categoryId }?.categoryTitle
            ?: ChemicalCategory.values().find { it.id == categoryId }?.title
            ?: categoryId

        // Optimistic synchronous UI state update
        _uiState.update { state ->
            val updatedProducts = state.selectedProductsInDb.map { product ->
                if (product.categoryId == categoryId) product.copy(isSelected = isSelected) else product
            }
            val updatedCategories = state.categorySelections.map { cat ->
                if (cat.categoryId == categoryId) {
                    val count = if (isSelected) updatedProducts.count { it.categoryId == categoryId } else 0
                    cat.copy(isSelected = isSelected, selectedProductCount = count)
                } else cat
            }
            state.copy(
                categorySelections = updatedCategories,
                selectedProductsInDb = updatedProducts,
                selectedCategoryCount = updatedCategories.count { it.isSelected },
                selectedProductCount = updatedProducts.count { it.isSelected },
                snackbarMessage = "${if (isSelected) "Selected" else "Deselected"} $categoryTitle in Room DB"
            )
        }

        // Persist to Room Database asynchronously
        viewModelScope.launch {
            categoryRepository?.setCategorySelected(categoryId, isSelected)
        }
    }

    /**
     * Toggles an individual chemical product's selection state in Room DB.
     */
    fun toggleProductSelectionInDb(productId: String, isSelected: Boolean) {
        val productName = _uiState.value.selectedProductsInDb.find { it.productId == productId }?.productName ?: productId

        // Optimistic synchronous UI update
        _uiState.update { state ->
            val updatedProducts = state.selectedProductsInDb.map {
                if (it.productId == productId) it.copy(isSelected = isSelected) else it
            }
            // Update parent category stats
            val targetCategory = updatedProducts.find { it.productId == productId }?.categoryId
            val updatedCategories = state.categorySelections.map { cat ->
                if (cat.categoryId == targetCategory) {
                    val catProducts = updatedProducts.filter { it.categoryId == targetCategory }
                    val selectedCount = catProducts.count { it.isSelected }
                    val allSelected = selectedCount == catProducts.size && catProducts.isNotEmpty()
                    cat.copy(selectedProductCount = selectedCount, isSelected = allSelected)
                } else cat
            }
            state.copy(
                selectedProductsInDb = updatedProducts,
                categorySelections = updatedCategories,
                selectedCategoryCount = updatedCategories.count { it.isSelected },
                selectedProductCount = updatedProducts.count { it.isSelected },
                snackbarMessage = "${if (isSelected) "Selected" else "Deselected"} $productName in Room DB"
            )
        }

        // Persist to Room Database asynchronously
        viewModelScope.launch {
            categoryRepository?.setProductSelected(productId, isSelected)
        }
    }

    /**
     * Bulk select or deselect all categories and products in Room DB.
     */
    fun selectAllInDb(isSelected: Boolean) {
        // Optimistic synchronous UI update
        _uiState.update { state ->
            val updatedProducts = state.selectedProductsInDb.map { it.copy(isSelected = isSelected) }
            val updatedCategories = state.categorySelections.map { cat ->
                val count = if (isSelected) updatedProducts.count { it.categoryId == cat.categoryId } else 0
                cat.copy(isSelected = isSelected, selectedProductCount = count)
            }
            state.copy(
                selectedProductsInDb = updatedProducts,
                categorySelections = updatedCategories,
                selectedCategoryCount = if (isSelected) updatedCategories.size else 0,
                selectedProductCount = if (isSelected) updatedProducts.size else 0,
                snackbarMessage = if (isSelected) "All categories and products selected in Room DB" else "Cleared all selections in Room DB"
            )
        }

        // Persist to Room Database asynchronously
        viewModelScope.launch {
            categoryRepository?.setAllSelected(isSelected)
        }
    }

    fun toggleCategoryExpanded(categoryId: String) {
        _uiState.update { state ->
            val updated = if (state.expandedCategoryIds.contains(categoryId)) {
                state.expandedCategoryIds - categoryId
            } else {
                state.expandedCategoryIds + categoryId
            }
            state.copy(expandedCategoryIds = updated)
        }
    }

    fun expandAllCategories() {
        _uiState.update { state ->
            state.copy(expandedCategoryIds = state.categorySelections.map { it.categoryId }.toSet())
        }
    }

    fun collapseAllCategories() {
        _uiState.update { state ->
            state.copy(expandedCategoryIds = emptySet())
        }
    }

    fun updateSelectionSearchQuery(query: String) {
        _uiState.update { it.copy(selectionSearchQuery = query) }
    }

    fun toggleFilterOnlySelected() {
        _uiState.update { it.copy(selectionFilterOnlySelected = !it.selectionFilterOnlySelected) }
    }

    /**
     * Converts all currently checked products in Room DB into Pending Inquiry records in Room DB.
     */
    fun addSelectedProductsToPendingInquiry(
        userName: String = "Valued Customer",
        companyName: String = "Industrial Mill",
        quantity: Int = 10,
        packagingUnit: String = "Carboy / Drum"
    ) {
        val selected = _uiState.value.selectedProductsInDb.filter { it.isSelected }
        if (selected.isEmpty()) {
            _uiState.update { it.copy(snackbarMessage = "No products selected. Please select categories or products first.") }
            return
        }

        selected.forEach { selectedProd ->
            val catalogProd = ChemicalCatalog.products.find { it.id == selectedProd.productId }
                ?: ChemicalProduct(
                    id = selectedProd.productId,
                    name = selectedProd.productName,
                    formula = selectedProd.chemicalFormula,
                    tag = selectedProd.categoryTitle,
                    category = ChemicalCategory.values().find { it.id == selectedProd.categoryId } ?: ChemicalCategory.ACIDS,
                    purity = selectedProd.purity.ifBlank { "Standard Industrial" },
                    availableGrades = listOf("Commercial / Industrial Grade"),
                    packagingOptions = listOf(selectedProd.packaging.ifBlank { "Standard Packaging" }),
                    applications = listOf("Industrial manufacturing"),
                    description = "Selected via Room DB Category Selection",
                    hazardType = "Industrial Chemical"
                )

            addPendingInquiry(
                userName = userName,
                companyName = companyName,
                quantity = quantity,
                product = catalogProd,
                unit = selectedProd.packaging.ifBlank { packagingUnit },
                grade = catalogProd.availableGrades.firstOrNull() ?: "Commercial Grade",
                notes = "Auto-generated from Room DB Product Category Selection"
            )
        }

        _uiState.update {
            it.copy(snackbarMessage = "Added ${selected.size} products from Room DB to Pending Inquiries!")
        }
    }
}
