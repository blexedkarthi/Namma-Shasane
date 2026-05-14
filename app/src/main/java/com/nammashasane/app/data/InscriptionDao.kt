package com.nammashasane.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InscriptionDao {
    @Query("SELECT * FROM inscriptions ORDER BY timestamp DESC")
    fun getAllInscriptions(): Flow<List<Inscription>>

    @Query("SELECT * FROM inscriptions WHERE id = :id")
    suspend fun getInscriptionById(id: Int): Inscription?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inscription: Inscription): Long

    @Update
    suspend fun update(inscription: Inscription)

    @Delete
    suspend fun delete(inscription: Inscription)

    @Query("SELECT * FROM inscriptions WHERE isDamaged = 1")
    fun getDamagedInscriptions(): Flow<List<Inscription>>
}
