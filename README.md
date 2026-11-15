# Práctica Calificada 02 (DSM) - Infraestructura Base (`feature/base`)

Este proyecto implementa la infraestructura compartida y la base de navegación para la **Práctica Calificada 02 (DSM)**, utilizando **Kotlin** y **Jetpack Compose**.

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
│   │   └── AppDatabase.kt        # Configuración base de Room
│   └── remote/
│       └── RetrofitClient.kt     # Clientes Retrofit con múltiples Base URLs
├── ui/
│   ├── navigation/
│   │   ├── NavGraph.kt           # Grafo de navegación Compose
│   │   └── Screen.kt             # Definición de rutas y destinos (APOD y NeoWs)
│   ├── screens/
│   │   ├── ApodPlaceholderScreen.kt   # Pantalla placeholder para APOD
│   │   └── NeoWsPlaceholderScreen.kt  # Pantalla placeholder para NeoWs
│   └── theme/                    # Tema Material 3
└── MainActivity.kt               # Contenedor principal con Scaffold y Bottom Navigation
```

---

## 🔑 Configuración de la API Key (`local.properties`)

Para acceder a las APIs de la NASA, es necesario configurar tu `NASA_API_KEY` en el archivo `local.properties` en la raíz del proyecto:

```properties
NASA_API_KEY=tu_api_key_aqui
```

Si no se especifica ninguna clave, el sistema utilizará `"DEMO_KEY"` por defecto. El archivo `local.properties` está excluido del control de versiones mediante `.gitignore`.

---

## 🛠️ Procedimiento de Integración para Ramas de Características (`feature/apod` y `feature/neows`)

Las ramas de características (`feature/apod` y `feature/neows`) deben construirse sobre esta infraestructura base (`feature/base`) siguiendo estas directrices:

1. **Creación de Modelos y Datos:**
   - Implementar DTOs y Servicios API utilizando las instancias de Retrofit (`RetrofitClient.scienceRetrofit` o `RetrofitClient.apiRetrofit`).
   - Crear las entidades (`@Entity`) y DAOs (`@Dao`) correspondientes a cada módulo.
2. **Integración en Base de Datos:**
   - Actualizar `AppDatabase` agregando las entidades correspondientes y los métodos abstractos para los DAOs.
3. **Repositorios y ViewModels:**
   - Implementar Repositorios que unifiquen el origen de datos remoto y local (Room).
   - Crear ViewModels utilizando `StateFlow` para exponer el estado UI de forma reactiva.
4. **Pantallas Funcionales:**
   - Reemplazar las pantallas placeholder (`ApodPlaceholderScreen` y `NeoWsPlaceholderScreen`) por las pantallas funcionales definitivas integradas al `NavGraph`.
