package com.example.pc2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ApodEntity::class], version = 1, exportSchema = false)
abstract class ApodDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao

    companion object {
        @Volatile
        private var instance: ApodDatabase? = null

        fun getDatabase(context: Context): ApodDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ApodDatabase::class.java,
                    "pc2_apod.db"
                ).build().also { instance = it }
            }
    }
}
