package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity representing the selection state of a chemical product category.
 */
@Entity(tableName = "category_selections")
data class CategorySelectionEntity(
    @PrimaryKey
    val categoryId: String,
    val categoryTitle: String,
    val isSelected: Boolean = false,
    val selectedProductCount: Int = 0,
    val totalProductCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
