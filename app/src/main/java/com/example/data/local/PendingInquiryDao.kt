package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing pending inquiries in the local Room database.
 */
@Dao
interface PendingInquiryDao {

    @Query("SELECT * FROM pending_inquiries ORDER BY timestamp DESC")
    fun getAllPendingInquiries(): Flow<List<PendingInquiryEntity>>

    @Query("SELECT * FROM pending_inquiries WHERE id = :id LIMIT 1")
    suspend fun getPendingInquiryById(id: Long): PendingInquiryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingInquiry(entity: PendingInquiryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<PendingInquiryEntity>)

    @Update
    suspend fun updatePendingInquiry(entity: PendingInquiryEntity)

    @Query("UPDATE pending_inquiries SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)

    @Delete
    suspend fun deletePendingInquiry(entity: PendingInquiryEntity)

    @Query("DELETE FROM pending_inquiries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM pending_inquiries")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM pending_inquiries")
    fun getPendingCount(): Flow<Int>
}
