package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity representing the selection state of an individual chemical product.
 */
@Entity(tableName = "selected_products")
data class SelectedProductEntity(
    @PrimaryKey
    val productId: String,
    val productName: String,
    val categoryId: String,
    val categoryTitle: String,
    val chemicalFormula: String = "",
    val purity: String = "",
    val packaging: String = "",
    val isSelected: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
