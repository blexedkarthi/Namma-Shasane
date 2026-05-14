package com.nammashasane.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Inscription::class], version = 1, exportSchema = false)
abstract class InscriptionDatabase : RoomDatabase() {
    abstract fun inscriptionDao(): InscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: InscriptionDatabase? = null

        fun getDatabase(context: Context): InscriptionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InscriptionDatabase::class.java,
                    "inscription_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                SeedData.inscriptions.forEach {
                                    database.inscriptionDao().insert(it)
                                }
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
