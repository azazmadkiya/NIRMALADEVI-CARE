package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val customerPhone: String = "",
    val deliveryLocation: String = "Morbi, Gujarat",
    val isUrgent: Boolean = false,
    val notes: String = "",
    val favorites: Set<String> = emptySet(),
    val dilutionState: DilutionState = DilutionState(),
    val massVolumeState: MassVolumeState = MassVolumeState(),
    val snackbarMessage: String? = null
)

class ChemicalViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChemicalUiState())
    val uiState: StateFlow<ChemicalUiState> = _uiState.asStateFlow()

    init {
        // Preload one sample item into quote so users can immediately test sending inquiry
        val initialProduct = ChemicalCatalog.products.first { it.id == "sulphuric-acid-comm" }
        _uiState.update { state ->
            state.copy(
                quoteItems = listOf(
                    QuoteItem(
                        product = initialProduct,
                        selectedGrade = initialProduct.availableGrades.first(),
                        quantity = 10,
                        unit = "Carboys (35 kg)",
                        isWholesale = true,
                        remarks = "Standard delivery at Morbi plant"
                    )
                )
            )
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

        val filtered = ChemicalCatalog.products.filter { product ->
            val matchesCategory = (category == ChemicalCategory.ALL) || (product.category == category)
            val matchesQuery = query.isEmpty() ||
                    product.name.lowercase().contains(query) ||
                    product.formula.lowercase().contains(query) ||
                    product.tag.lowercase().contains(query) ||
                    product.purity.lowercase().contains(query) ||
                    product.applications.any { it.lowercase().contains(query) }
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

    fun updateCustomerInfo(name: String, phone: String, location: String, urgent: Boolean, notes: String) {
        _uiState.update { state ->
            state.copy(
                customerName = name,
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
}
