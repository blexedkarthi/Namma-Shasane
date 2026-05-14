package com.nammashasane.app.data

import kotlinx.coroutines.flow.Flow

class InscriptionRepository(private val dao: InscriptionDao) {
    val allInscriptions: Flow<List<Inscription>> = dao.getAllInscriptions()
    val damagedInscriptions: Flow<List<Inscription>> = dao.getDamagedInscriptions()

    suspend fun getById(id: Int): Inscription? = dao.getInscriptionById(id)
    suspend fun insert(inscription: Inscription) = dao.insert(inscription)
    suspend fun update(inscription: Inscription) = dao.update(inscription)
    suspend fun delete(inscription: Inscription) = dao.delete(inscription)
}
