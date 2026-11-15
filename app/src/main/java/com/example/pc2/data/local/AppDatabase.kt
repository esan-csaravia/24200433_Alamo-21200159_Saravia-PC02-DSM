package com.example.pc2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pc2.data.local.neows.AsteroidDao
import com.example.pc2.data.local.neows.AsteroidEntity

@Database(entities = [ApodEntity::class, AsteroidEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
    abstract fun asteroidDao(): AsteroidDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nasa_shared_database"
                ).build().also { instance = it }
            }

        fun getInstance(context: Context): AppDatabase = getDatabase(context)
    }
}
