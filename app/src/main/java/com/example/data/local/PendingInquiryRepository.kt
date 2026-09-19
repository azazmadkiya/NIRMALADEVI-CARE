package com.example.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern implementation abstracting the Room DAO for ViewModel & UI consumption.
 */
class PendingInquiryRepository(private val dao: PendingInquiryDao) {

    val allPendingInquiries: Flow<List<PendingInquiryEntity>> = dao.getAllPendingInquiries()
    val pendingCount: Flow<Int> = dao.getPendingCount()

    suspend fun insert(entity: PendingInquiryEntity): Long {
        return dao.insertPendingInquiry(entity)
    }

    suspend fun insertAll(entities: List<PendingInquiryEntity>) {
        dao.insertAll(entities)
    }

    suspend fun update(entity: PendingInquiryEntity) {
        dao.updatePendingInquiry(entity)
    }

    suspend fun updateQuantity(id: Long, quantity: Int) {
        dao.updateQuantity(id, quantity)
    }

    suspend fun delete(entity: PendingInquiryEntity) {
        dao.deletePendingInquiry(entity)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
