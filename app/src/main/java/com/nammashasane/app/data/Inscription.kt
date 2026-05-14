package com.nammashasane.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inscriptions")
data class Inscription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dynasty: String,
    val period: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val translationKannada: String,
    val photoUri: String = "",
    val isUserAdded: Boolean = false,
    val isDamaged: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
