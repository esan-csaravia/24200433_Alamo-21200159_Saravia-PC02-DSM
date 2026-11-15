package com.example.pc2.data.local

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pc2.data.local.neows.AsteroidDao
import com.example.pc2.data.local.neows.AsteroidEntity

@Database(entities = [ApodEntity::class, AsteroidEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
    abstract fun asteroidDao(): AsteroidDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS asteroids (
                        id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL,
                        closeApproachDate TEXT NOT NULL, estimatedDiameterMin REAL NOT NULL,
                        estimatedDiameterMax REAL NOT NULL, relativeVelocity REAL NOT NULL,
                        missDistance REAL NOT NULL, isPotentiallyHazardous INTEGER NOT NULL,
                        isFavorite INTEGER NOT NULL)"""
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: createDatabase(context, "pc2_apod.db", "nasa_shared_database")
                    .also { instance = it }
            }

        fun getInstance(context: Context): AppDatabase = getDatabase(context)

        internal fun createDatabase(context: Context, name: String, legacyName: String): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, name)
                .addMigrations(migration1To2)
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        val legacyFile = context.getDatabasePath(legacyName)
                        if (!legacyFile.exists() || name == legacyName) return
                        SQLiteDatabase.openDatabase(
                            legacyFile.path, null, SQLiteDatabase.OPEN_READONLY
                        ).use { legacy ->
                            copyTable(legacy, db, "apod_images",
                                "date, title, explanation, mediaType, hdUrl, credit, isFavorite")
                            copyTable(legacy, db, "asteroids",
                                "id, name, closeApproachDate, estimatedDiameterMin, estimatedDiameterMax, relativeVelocity, missDistance, isPotentiallyHazardous, isFavorite")
                        }
                    }
                }).build()

        private fun copyTable(
            source: SQLiteDatabase,
            destination: SupportSQLiteDatabase,
            table: String,
            columns: String
        ) {
            source.rawQuery(
                "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?",
                arrayOf(table)
            ).use { if (!it.moveToFirst()) return }
            source.rawQuery("SELECT $columns FROM $table", null).use { cursor ->
                val placeholders = List(cursor.columnCount) { "?" }.joinToString()
                while (cursor.moveToNext()) {
                    val values = Array<Any?>(cursor.columnCount) { index ->
                        when (cursor.getType(index)) {
                            Cursor.FIELD_TYPE_NULL -> null
                            Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(index)
                            Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(index)
                            Cursor.FIELD_TYPE_STRING -> cursor.getString(index)
                            Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(index)
                            else -> error("Tipo de columna SQLite no soportado")
                        }
                    }
                    destination.execSQL("INSERT OR IGNORE INTO $table ($columns) VALUES ($placeholders)", values)
                }
            }
        }
    }
}
