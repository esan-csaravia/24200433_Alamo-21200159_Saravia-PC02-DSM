# Práctica Calificada 02 (DSM) - Diario espacial

Este proyecto combina la infraestructura compartida con la opción de imágenes APOD para la **Práctica Calificada 02 (DSM)**, utilizando **Kotlin** y **Jetpack Compose**. La opción de asteroides permanece como pantalla pendiente.

---

## 🚀 Arquitectura y Tecnologías
- **UI Declarativa:** Jetpack Compose & Material 3.
- **Navegación:** Jetpack Navigation Compose (`NavGraph`, `NavigationBar`, `Scaffold`).
- **Networking:** Retrofit con soporte para múltiples URLs base (`https://science.nasa.gov/` y `https://api.nasa.gov/`).
- **Persistencia:** Room (configuración base y convenciones compartidas).
- **Concurrencia:** Kotlin Coroutines & StateFlow.
- **Configuración de Credenciales:** Lectura automática de `NASA_API_KEY` desde `local.properties` expuesta mediante `BuildConfig`.

---

## 📂 Estructura del Proyecto

```text
com.example.pc2/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt        # Base Room compartida
│   │   ├── ApodDao.kt
│   │   └── ApodEntity.kt
│   ├── remote/
│   │   ├── RetrofitClient.kt     # Clientes Retrofit con múltiples Base URLs
│   │   ├── ApodApi.kt
│   │   └── ApodDto.kt
│   └── repository/
│       └── ApodRepository.kt
├── presentation/
│   ├── apod/                     # Pantallas y ViewModel de APOD
│   └── navigation/
│       └── AppNavGraph.kt        # Grafo y barra de navegación
├── ui/
│   ├── navigation/
│   │   └── Screen.kt             # Rutas principales de imágenes y asteroides
│   ├── screens/
│   │   └── NeoWsPlaceholderScreen.kt  # Pantalla pendiente para asteroides
│   └── theme/                    # Tema Material 3
└── MainActivity.kt               # Configuración y entrada a la aplicación
```

---

## 🔑 Configuración de la API Key (`local.properties`)

Para acceder a las APIs de la NASA, es necesario configurar tu `NASA_API_KEY` en el archivo `local.properties` en la raíz del proyecto:

```properties
NASA_API_KEY=tu_api_key_aqui
```

La aplicación muestra un error si no se configura la clave; no utiliza `DEMO_KEY`. El archivo `local.properties` está excluido del control de versiones mediante `.gitignore`. La clave se incorpora al APK mediante `BuildConfig`, por lo que no es secreta frente a quien tenga acceso al APK.

---

## 🛠️ Procedimiento de Integración para Ramas de Características (`feature/apod` y `feature/neows`)

Las ramas de características deben integrarse sobre la infraestructura compartida siguiendo estas directrices:

1. **Creación de Modelos y Datos:**
   - Implementar DTOs y Servicios API utilizando las instancias de Retrofit (`RetrofitClient.scienceRetrofit` o `RetrofitClient.apiRetrofit`).
   - Crear las entidades (`@Entity`) y DAOs (`@Dao`) correspondientes a cada módulo.
2. **Integración en Base de Datos:**
   - Actualizar `AppDatabase` agregando las entidades correspondientes y los métodos abstractos para los DAOs.
3. **Repositorios y ViewModels:**
   - Implementar Repositorios que unifiquen el origen de datos remoto y local (Room).
   - Crear ViewModels utilizando `StateFlow` para exponer el estado UI de forma reactiva.
4. **Pantallas Funcionales:**
   - Mantener las pantallas APOD integradas en `AppNavGraph` y sustituir `NeoWsPlaceholderScreen` cuando se implemente la opción de asteroides.
