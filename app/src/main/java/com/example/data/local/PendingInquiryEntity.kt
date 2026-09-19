package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity representing an item or product selected for a customer inquiry.
 */
@Entity(tableName = "pending_inquiries")
data class PendingInquiryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userName: String,
    val companyName: String,
    val quantity: Int,
    val productId: String = "",
    val productName: String,
    val productCategory: String = "Industrial Chemicals",
    val chemicalFormula: String = "",
    val grade: String = "Industrial Grade",
    val packagingUnit: String = "Carboy (30-50 kg)",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
