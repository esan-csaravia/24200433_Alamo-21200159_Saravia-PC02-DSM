package com.example.pc2.data.local

import androidx.room.RoomDatabase

/**
 * Infraestructura base de Room compartida.
 * Las anotaciones @Database, Entities y DAOs definitivos serán implementados
 * e integrados conjuntamente por los módulos APOD y NeoWs en sus respectivas ramas.
 */
abstract class AppDatabase : RoomDatabase() {
    // Convenciones compartidas de Room para la base de datos centralizada de la app.
}
