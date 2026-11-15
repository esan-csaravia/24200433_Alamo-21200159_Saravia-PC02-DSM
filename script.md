# Script Plantilla: AppDPA (Android Jetpack Compose + Room + Retrofit + Firebase + Supabase)

Este documento sirve como **script y plantilla de referencia** para recrear desde cero la estructura, dependencias, configuraciones y componentes clave de la aplicación **AppDPA**.

---

## 1. Estructura de Paquetes (`dev.lchang.appdpa`)

```text
dev.lchang.appdpa/
├── data/
│   ├── local/            # Room Database (AppDatabase, DAOs, Entities)
│   ├── model/            # Modelos de datos compartidos
│   └── remote/           # Clientes API (Firebase, Supabase, Retrofit / API Football)
├── presentation/
│   ├── apifootball/      # Pantalla y ViewModel de API Football
│   ├── auth/             # Login y Registro (Firebase Auth + Términos WebView)
│   ├── favorites/        # Pantalla de favoritos
│   ├── home/             # Pantalla principal (Home)
│   ├── navigation/       # AppNavGraph y DrawerScaffold
│   ├── permissions/      # Permisos de galería
│   ├── realtime/         # Firestore Realtime
│   └── supabase/         # Supabase Storage e imágenes
└── ui/
    └── theme/            # Tema Material 3 (Color, Type, Theme)
```

---

## 2. Configuración de Gradle y Dependencias

### `gradle/libs.versions.toml`
```toml
[versions]
agp = "8.8.0"
kotlin = "2.0.0"
coreKtx = "1.15.0"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.10.0"
composeBom = "2025.02.00"
firebaseBom = "33.9.0"
supabase = "3.1.1"
ktor = "3.1.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }

# Firebase
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }

# Supabase
supabase-bom = { group = "io.github.jan-tennert.supabase", name = "bom", version.ref = "supabase" }
supabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt" }
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }
ktor-client-cio = { group = "io.ktor", name = "ktor-client-cio", version.ref = "ktor" }

# Retrofit
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version = "2.11.0" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version = "3.0.0" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version = "5.5.1" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

### `app/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "dev.lchang.appdpa"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.lchang.appdpa"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.10.1")

    // Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Supabase & Ktor
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.storage)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp-logging)

    // Room Database
    val roomVersion = "2.8.5"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.7.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso-core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test-manifest)
}
```

---

## 3. Capa de Datos y Servicios Reutilizables

### `data/remote/SupabaseManager.kt`
```kotlin
package dev.lchang.appdpa.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseManager {
    private const val SUPABASE_URL = "https://tthgdqvcllxqspwwcyeh.supabase.co"
    private const val SUPABASE_ANON_KEY = "TU_SUPABASE_ANON_KEY"

    val client = createSupabaseClient(
        SUPABASE_URL,
        SUPABASE_ANON_KEY
    ) {
        install(Storage)
    }
}
```

### `data/remote/FirebaseAuthManager.kt`
```kotlin
package dev.lchang.appdpa.data.remote

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthManager {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
}
```

### `data/remote/apifootball/RetrofitInstance.kt`
```kotlin
package dev.lchang.appdpa.data.remote.apifootball

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-apisports-key", "TU_API_KEY_AQUI")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: ApiFootballService by lazy {
        Retrofit.Builder()
            .baseUrl("https://v3.football.api-sports.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiFootballService::class.java)
    }
}
```

---

## 4. Navegación y Estructura UI

### `presentation/navigation/DrawerScaffold.kt`
```kotlin
package dev.lchang.appdpa.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScaffold(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("Menu principal", modifier = Modifier.padding(16.dp))

                NavigationDrawerItem(label = { Text("Home") }, selected = false, onClick = { navController.navigate("home") })
                NavigationDrawerItem(label = { Text("Permissions") }, selected = false, onClick = { navController.navigate("permissions") })
                NavigationDrawerItem(label = { Text("Favorites") }, selected = false, onClick = { navController.navigate("favorites") })
                NavigationDrawerItem(label = { Text("Realtime") }, selected = false, onClick = { navController.navigate("realtime") })
                NavigationDrawerItem(label = { Text("Supabase Storage") }, selected = false, onClick = { navController.navigate("supabase") })
                NavigationDrawerItem(label = { Text("API Football") }, selected = false, onClick = { navController.navigate("apifootball") })
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AppDPA") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                content()
            }
        }
    }
}
```

### `presentation/navigation/AppNavGraph.kt`
```kotlin
package dev.lchang.appdpa.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.lchang.appdpa.presentation.apifootball.ApiFootballScreen
import dev.lchang.appdpa.presentation.auth.LoginScreen
import dev.lchang.appdpa.presentation.auth.RegisterScreen
import dev.lchang.appdpa.presentation.favorites.FavoritesScreen
import dev.lchang.appdpa.presentation.home.HomeScreen
import dev.lchang.appdpa.presentation.permissions.GalleryPermissionsScreen
import dev.lchang.appdpa.presentation.realtime.FirestoreRealtimeScreen
import dev.lchang.appdpa.presentation.supabase.SupabaseStorageScreen

@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "register")
    {
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { DrawerScaffold(navController) { HomeScreen() } }
        composable("permissions") { DrawerScaffold(navController) { GalleryPermissionsScreen() } }
        composable("favorites") { DrawerScaffold(navController) { FavoritesScreen() } }
        composable("realtime") { DrawerScaffold(navController) { FirestoreRealtimeScreen() } }
        composable("supabase") { DrawerScaffold(navController) { SupabaseStorageScreen() } }
        composable("apifootball") { DrawerScaffold(navController) { ApiFootballScreen() } }
    }
}
```

---

## 5. Resumen de Características de la Plantilla
- **Autenticación con Firebase Auth** (`LoginScreen`, `RegisterScreen` con validación de términos y condiciones en `WebView`).
- **Base de Datos Local (Room)** configurada con KSP.
- **Consumo de APIs REST (Retrofit + OkHttp)** con interceptores (ej. API Football).
- **Backend as a Service (Supabase Storage)** para carga y visualización de imágenes.
- **Bases de datos en tiempo real (Firebase Firestore)**.
- **Navegación robusta con Jetpack Compose Navigation 2.x** y un contenedor reutilizable de menú lateral (`ModalNavigationDrawer` / `DrawerScaffold`).
