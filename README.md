# BRODCAST - Aplicación de Monitoreo de SMS

![Android](https://img.shields.io/badge/Android-16-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple)
![License](https://img.shields.io/badge/License-MIT-blue)

## 📋 Descripción del Proyecto

**BRODCAST** es una aplicación Android desarrollada en Kotlin que demuestra la integración con servicios del dispositivo mediante el uso de **Broadcast Receivers** para interceptar y filtrar mensajes SMS entrantes de números específicos autorizados. La aplicación implementa patrones de seguridad, manejo de permisos en tiempo de ejecución y logging detallado para debugging.

---

## 🎓 Preguntas Teóricas y Respuestas

### 1. ¿Cómo se pueden desarrollar aplicaciones que se integren con los servicios del dispositivo, como la batería, los mensajes o las notificaciones, de forma eficiente y segura, considerando diferentes tipos de servicios, permisos y la experiencia del usuario?

#### Respuesta:

**Integración Eficiente y Segura con Servicios del Dispositivo:**

1. **Sistema de Permisos en Tiempo de Ejecución (Runtime Permissions):**
   - Android 6.0+ requiere solicitar permisos peligrosos en tiempo de ejecución, no solo en el manifiesto
   - Implementar verificación con `ContextCompat.checkSelfPermission()` antes de acceder a recursos sensibles
   - Usar `ActivityCompat.requestPermissions()` y manejar la respuesta en `onRequestPermissionsResult()`
   - Proporcionar contexto al usuario sobre por qué se necesita el permiso (mejora UX)

2. **Tipos de Servicios y su Implementación:**
   - **Servicios en Primer Plano (Foreground Services):** Para tareas de larga duración visibles al usuario (música, seguimiento GPS)
   - **Servicios en Segundo Plano (Background Services):** Limitados desde Android 8.0+ por restricciones de batería
   - **JobScheduler/WorkManager:** Para tareas diferibles que respetan las políticas de batería del sistema
   - **Broadcast Receivers:** Para responder a eventos del sistema (SMS, batería, conectividad)

3. **Eficiencia Energética:**
   - Usar `BatteryManager` para monitorear el estado de la batería antes de operaciones intensivas
   - Implementar `Doze Mode` awareness: usar `WorkManager` para tareas que deben ejecutarse en modo de bajo consumo
   - Agrupar operaciones de red (batching) para minimizar el uso de radio
   - Liberar recursos cuando no se necesitan (sensores, GPS, conexiones)

4. **Seguridad:**
   - **Validar el origen de los Intents:** Usar permisos personalizados para Broadcast Receivers
   - **No exponer componentes innecesariamente:** `android:exported="false"` cuando no se requiere acceso externo
   - **Validar datos de entrada:** Nunca confiar en datos externos sin validación
   - **Cifrado de datos sensibles:** Usar `EncryptedSharedPreferences` o `KeyStore` para credenciales

5. **Experiencia del Usuario:**
   - **Notificaciones claras y accionables:** Usar `NotificationCompat` con actions relevantes
   - **Indicadores visuales:** Progress bars para operaciones largas
   - **Degradación elegante:** Funcionalidad limitada si se niegan permisos, no crashes
   - **Feedback inmediato:** Toast, Snackbar o notificaciones para confirmar acciones

**Ejemplo en este proyecto:**
```kotlin
// Verificación de permisos
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
    != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(
        this, 
        arrayOf(Manifest.permission.RECEIVE_SMS), 
        SMS_PERMISSION_REQUEST_CODE
    )
}

// Manejo de respuesta
override fun onRequestPermissionsResult(...) {
    when (requestCode) {
        SMS_PERMISSION_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                // Permiso otorgado - funcionalidad completa
            } else {
                // Permiso denegado - informar al usuario
            }
        }
    }
}
```

---

### 2. ¿Cómo se pueden utilizar Broadcast Intents y Broadcast Receivers para comunicar diferentes aplicaciones entre sí de forma segura y eficiente, considerando diferentes tipos de acciones, datos y la experiencia del usuario?

#### Respuesta:

**Comunicación Segura y Eficiente con Broadcasts:**

1. **Tipos de Broadcasts:**

   a) **Broadcasts Normales (Normal Broadcasts):**
   - Enviados con `sendBroadcast(intent)`
   - Todos los receivers registrados reciben el broadcast de forma asíncrona
   - No hay garantía de orden
   
   b) **Broadcasts Ordenados (Ordered Broadcasts):**
   - Enviados con `sendOrderedBroadcast(intent, permission)`
   - Se entregan a un receiver a la vez, en orden de prioridad
   - Un receiver puede cancelar el broadcast o modificar los datos
   
   c) **Broadcasts Locales (Local Broadcasts):**
   - Usan `LocalBroadcastManager` (deprecated, usar LiveData/Flow ahora)
   - Solo dentro de la misma aplicación - más eficiente y seguro
   
   d) **Broadcasts Sticky (obsoleto desde API 21):**
   - Permanecen en el sistema después de ser enviados

2. **Seguridad en Broadcasts:**

   a) **Permisos Personalizados:**
   ```xml
   <!-- Definir permiso personalizado -->
   <permission 
       android:name="com.example.brodcast.RECEIVE_SMS_NOTIFICATION"
       android:protectionLevel="signature" />
   
   <!-- Requerir permiso para enviar -->
   <receiver 
       android:name=".SmsReceiver"
       android:permission="android.permission.BROADCAST_SMS">
   ```

   b) **Receivers No Exportados:**
   ```xml
   <receiver 
       android:name=".InternalReceiver"
       android:exported="false"> <!-- Solo accesible desde la app -->
   ```

   c) **Validación de Origen:**
   ```kotlin
   override fun onReceive(context: Context, intent: Intent) {
       // Validar el origen del intent
       val callingUid = Binder.getCallingUid()
       if (!isAuthorizedApp(callingUid)) {
           Log.w(TAG, "Unauthorized broadcast ignored")
           return
       }
   }
   ```

3. **Eficiencia:**

   a) **Registro Dinámico vs Estático:**
   - **Estático (Manifest):** Para eventos del sistema críticos (BOOT_COMPLETED, SMS_RECEIVED)
   - **Dinámico (registerReceiver):** Para eventos durante el ciclo de vida de la app
   ```kotlin
   // Registro dinámico
   val filter = IntentFilter("com.example.CUSTOM_ACTION")
   registerReceiver(myReceiver, filter)
   
   // IMPORTANTE: Desregistrar en onPause/onDestroy
   unregisterReceiver(myReceiver)
   ```

   b) **Limitaciones desde Android 8.0+:**
   - La mayoría de broadcasts implícitos no funcionan desde el manifest
   - Usar `JobScheduler` o `WorkManager` para tareas diferidas
   - Excepciones: SMS_RECEIVED, BOOT_COMPLETED aún funcionan

   c) **Procesamiento Ligero:**
   ```kotlin
   override fun onReceive(context: Context, intent: Intent) {
       // NUNCA hacer operaciones largas aquí
       // Timeout de ~10 segundos en onReceive
       
       // Opción 1: Usar WorkManager para trabajo pesado
       val workRequest = OneTimeWorkRequestBuilder<SmsProcessWorker>()
           .setInputData(workDataOf("sms_data" to intent.getStringExtra("data")))
           .build()
       WorkManager.getInstance(context).enqueue(workRequest)
       
       // Opción 2: Usar goAsync() si necesitas más tiempo
       val pendingResult = goAsync()
       thread {
           // Trabajo en background
           pendingResult.finish()
       }
   }
   ```

4. **Comunicación Entre Apps:**

   a) **Intent Explícito (más seguro):**
   ```kotlin
   val intent = Intent()
   intent.setClassName("com.example.otherapp", "com.example.otherapp.MyReceiver")
   intent.putExtra("data", "value")
   sendBroadcast(intent, "com.example.CUSTOM_PERMISSION")
   ```

   b) **Intent Implícito con Filtro:**
   ```kotlin
   val intent = Intent("com.example.CUSTOM_ACTION")
   intent.putExtra("data", "value")
   sendBroadcast(intent)
   ```

5. **Experiencia del Usuario:**
   - **Notificaciones no intrusivas:** Usar canales de notificación apropiados
   - **Agrupación de notificaciones:** Para múltiples broadcasts similares
   - **Prioridad correcta:** No interrumpir al usuario innecesariamente
   - **Acciones directas:** Permitir responder desde la notificación

**Ejemplo en este proyecto:**
```kotlin
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Validar acción
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        
        // 2. Extraer datos de forma segura
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        
        // 3. Procesar eficientemente
        messages?.forEach { smsMessage ->
            val sender = smsMessage.originatingAddress
            
            // 4. Filtrar por lista blanca (seguridad)
            if (phoneNumbers.contains(sender)) {
                // 5. Feedback al usuario
                Toast.makeText(context, "SMS de $sender", Toast.LENGTH_LONG).show()
            }
        }
    }
}
```

---

### 3. ¿Cómo se pueden utilizar hilos en Android para mejorar la eficiencia y la capacidad de respuesta de una aplicación, considerando diferentes tipos de tareas, la sincronización de datos y la experiencia del usuario?

#### Respuesta:

**Threading en Android para Aplicaciones Responsivas:**

1. **Regla de Oro: Main Thread (UI Thread)**
   - **NUNCA bloquear el UI Thread:** Operaciones >16ms causan frame drops
   - **Solo el UI Thread puede modificar la UI:** Excepciones causan `CalledFromWrongThreadException`
   - **ANR (Application Not Responding):** Si el UI Thread se bloquea >5 segundos

2. **Tipos de Tareas y Estrategias:**

   a) **Tareas de Corta Duración (<100ms):**
   ```kotlin
   // Opción 1: Thread básico
   Thread {
       // Trabajo en background
       runOnUiThread {
           // Actualizar UI
       }
   }.start()
   
   // Opción 2: Handler + Looper
   val handler = Handler(Looper.getMainLooper())
   thread {
       val result = doWork()
       handler.post {
           updateUI(result)
       }
   }
   ```

   b) **Tareas Asíncronas (Coroutines - Recomendado):**
   ```kotlin
   class MyViewModel : ViewModel() {
       fun loadData() {
           viewModelScope.launch {
               // Se ejecuta en Main (UI-safe)
               progressBar.visibility = View.VISIBLE
               
               // Cambiar a IO para operaciones pesadas
               val result = withContext(Dispatchers.IO) {
                   // Red, base de datos, archivo
                   repository.fetchData()
               }
               
               // Automáticamente vuelve al Main thread
               updateUI(result)
               progressBar.visibility = View.GONE
           }
       }
   }
   ```

   c) **Tareas Diferibles (WorkManager):**
   ```kotlin
   val uploadWork = OneTimeWorkRequestBuilder<UploadWorker>()
       .setConstraints(Constraints.Builder()
           .setRequiredNetworkType(NetworkType.CONNECTED)
           .setRequiresBatteryNotLow(true)
           .build())
       .build()
   
   WorkManager.getInstance(context).enqueue(uploadWork)
   ```

   d) **Tareas Periódicas:**
   ```kotlin
   val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
       .build()
   WorkManager.getInstance(context).enqueue(syncWork)
   ```

3. **Dispatchers en Kotlin Coroutines:**

   ```kotlin
   // Dispatchers.Main - UI operations
   launch(Dispatchers.Main) {
       textView.text = "Loading..."
   }
   
   // Dispatchers.IO - Network, Database, File I/O
   withContext(Dispatchers.IO) {
       val data = database.query()
       val response = apiService.fetchData()
   }
   
   // Dispatchers.Default - CPU-intensive work
   withContext(Dispatchers.Default) {
       val result = complexCalculation()
       val processed = processLargeList()
   }
   
   // Dispatchers.Unconfined - No recomendado generalmente
   ```

4. **Sincronización de Datos:**

   a) **Thread-Safe con Synchronized:**
   ```kotlin
   class Counter {
       private var count = 0
       
       @Synchronized
       fun increment() {
           count++
       }
       
       @Synchronized
       fun getCount() = count
   }
   ```

   b) **Atomic Variables:**
   ```kotlin
   class ThreadSafeCounter {
       private val count = AtomicInteger(0)
       
       fun increment() = count.incrementAndGet()
       fun get() = count.get()
   }
   ```

   c) **Coroutines con Mutex:**
   ```kotlin
   class SharedResource {
       private val mutex = Mutex()
       private var data = 0
       
       suspend fun updateData(newValue: Int) {
           mutex.withLock {
               data = newValue
           }
       }
   }
   ```

   d) **StateFlow/SharedFlow (Reactive):**
   ```kotlin
   class MyViewModel : ViewModel() {
       private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
       val uiState: StateFlow<UiState> = _uiState.asStateFlow()
       
       fun loadData() {
           viewModelScope.launch {
               _uiState.value = UiState.Loading
               try {
                   val data = repository.getData()
                   _uiState.value = UiState.Success(data)
               } catch (e: Exception) {
                   _uiState.value = UiState.Error(e.message)
               }
           }
       }
   }
   ```

5. **Patrones para Operaciones de Red:**

   ```kotlin
   // Patrón Repository con Coroutines
   class SmsRepository(private val apiService: ApiService) {
       
       suspend fun sendSmsToServer(sms: SmsData): Result<Response> = withContext(Dispatchers.IO) {
           try {
               val response = apiService.sendSms(sms)
               Result.success(response)
           } catch (e: Exception) {
               Result.failure(e)
           }
       }
   }
   
   // ViewModel
   class SmsViewModel(private val repository: SmsRepository) : ViewModel() {
       
       fun processSms(sms: SmsData) {
           viewModelScope.launch {
               _isLoading.value = true
               
               repository.sendSmsToServer(sms)
                   .onSuccess { response ->
                       _message.value = "SMS enviado exitosamente"
                   }
                   .onFailure { error ->
                       _error.value = "Error: ${error.message}"
                   }
               
               _isLoading.value = false
           }
       }
   }
   ```

6. **Experiencia del Usuario:**

   a) **Feedback Visual:**
   ```kotlin
   fun loadData() {
       lifecycleScope.launch {
           // Mostrar loading
           binding.progressBar.visibility = View.VISIBLE
           binding.contentLayout.alpha = 0.5f
           
           val data = withContext(Dispatchers.IO) {
               // Operación pesada
               repository.fetchData()
           }
           
           // Ocultar loading
           binding.progressBar.visibility = View.GONE
           binding.contentLayout.alpha = 1.0f
           
           // Actualizar UI
           updateContent(data)
       }
   }
   ```

   b) **Cancelación de Operaciones:**
   ```kotlin
   class SearchViewModel : ViewModel() {
       private var searchJob: Job? = null
       
       fun search(query: String) {
           searchJob?.cancel() // Cancelar búsqueda anterior
           searchJob = viewModelScope.launch {
               delay(300) // Debounce
               val results = repository.search(query)
               _searchResults.value = results
           }
       }
   }
   ```

   c) **Manejo de Lifecycle:**
   ```kotlin
   class MyActivity : AppCompatActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           
           // Se cancela automáticamente cuando la Activity se destruye
           lifecycleScope.launch {
               repeatOnLifecycle(Lifecycle.State.STARTED) {
                   viewModel.uiState.collect { state ->
                       updateUI(state)
                   }
               }
           }
       }
   }
   ```

**Ejemplo aplicado al proyecto:**
```kotlin
// Si necesitáramos enviar SMS a un servidor
class MainActivity : AppCompatActivity() {
    
    private fun processSmsInBackground(sms: SmsData) {
        lifecycleScope.launch {
            try {
                // Mostrar indicador de carga
                showLoading(true)
                
                // Operación en background thread
                val result = withContext(Dispatchers.IO) {
                    // Simular procesamiento pesado
                    delay(2000)
                    uploadSmsToServer(sms)
                }
                
                // Actualizar UI en Main thread
                Toast.makeText(this@MainActivity, "SMS procesado: $result", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error procesando SMS", e)
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                showLoading(false)
            }
        }
    }
    
    private suspend fun uploadSmsToServer(sms: SmsData): String {
        // Operación de red en IO thread
        return "Success"
    }
}
```

**Mejores Prácticas:**
- ✅ Usar Kotlin Coroutines con `viewModelScope` o `lifecycleScope`
- ✅ Nunca hacer operaciones de red/DB en el Main thread
- ✅ Proporcionar feedback visual para operaciones largas
- ✅ Cancelar operaciones cuando ya no son necesarias
- ✅ Manejar errores y excepciones apropiadamente
- ❌ No usar `AsyncTask` (deprecated desde API 30)
- ❌ No crear threads sin control (memory leaks)

---

## 💭 Reflexión Personal

El desarrollo de esta aplicación BRODCAST me ha permitido comprender profundamente los conceptos fundamentales de la programación Android, especialmente en lo referente a la integración con servicios del sistema operativo. La implementación de Broadcast Receivers para interceptar SMS ha sido un ejercicio revelador sobre la arquitectura de componentes de Android y la importancia del modelo de seguridad basado en permisos.

Me sorprendió la complejidad del sistema de permisos en tiempo de ejecución introducido desde Android 6.0, y cómo este mejora significativamente la privacidad del usuario al darle control granular sobre qué información comparte con las aplicaciones. El debugging detallado implementado en el proyecto me enseñó la importancia de tener visibilidad completa del flujo de ejecución, especialmente cuando se trabaja con componentes asíncronos como los receivers.

La experiencia de corregir el error crítico en el AndroidManifest.xml (`android.icon` vs `android:icon`) me recordó que incluso los errores más pequeños pueden tener consecuencias devastadoras en aplicaciones móviles, donde el usuario no tiene la paciencia de esperar o intentar múltiples veces. Esta lección sobre la importancia de la validación y testing exhaustivo es invaluable.

Además, trabajar con Kotlin en lugar de Java me ha mostrado cómo un lenguaje moderno puede hacer el código más expresivo, seguro (null safety) y mantenible. Las coroutines de Kotlin representan un cambio paradigmático en cómo manejamos la asincronía, haciendo el código más legible que los callbacks tradicionales o AsyncTask.

Finalmente, entiendo que el desarrollo Android moderno requiere un balance constante entre funcionalidad, rendimiento, seguridad y experiencia de usuario. Cada decisión técnica tiene implicaciones en cómo el usuario percibe y utiliza la aplicación. Este proyecto es solo el comienzo de un aprendizaje continuo en un ecosistema que evoluciona constantemente.

---

## 🏗️ Arquitectura del Proyecto

```
BRODCAST/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml          # Configuración de permisos y componentes
│   │   │   ├── java/com/example/brodcast/
│   │   │   │   ├── MainActivity.kt          # Activity principal con ListView
│   │   │   │   └── SmsReceiver.kt           # BroadcastReceiver para SMS
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       │   └── activity_main.xml    # Layout con ListView
│   │   │       ├── values/
│   │   │       │   ├── strings.xml
│   │   │       │   ├── colors.xml
│   │   │       │   └── themes.xml
│   │   │       └── mipmap/                  # Iconos de la aplicación
│   │   ├── test/                            # Pruebas unitarias
│   │   └── androidTest/                     # Pruebas instrumentadas
│   └── build.gradle.kts                     # Configuración de dependencias
├── gradle/
│   └── libs.versions.toml                   # Catálogo de versiones
├── DEBUG_INSTRUCTIONS.md                    # Guía de debugging
├── RESUMEN_CORRECCIONES.md                  # Documentación de fixes
└── README.md                                # Este archivo
```

---

## 🚀 Características Principales

### ✅ Funcionalidades Implementadas

1. **Intercepción de SMS**
   - Broadcast Receiver registrado para `SMS_RECEIVED_ACTION`
   - Filtrado por lista de números autorizados
   - Notificaciones Toast para SMS entrantes

2. **Gestión de Permisos**
   - Solicitud de permiso `RECEIVE_SMS` en tiempo de ejecución
   - Manejo de respuesta del usuario con feedback
   - Verificación previa antes de acceder al servicio

3. **Interfaz de Usuario**
   - ListView mostrando números autorizados
   - Activity con Material Design
   - Notificaciones Toast para feedback inmediato

4. **Logging y Debugging**
   - Logs detallados del ciclo de vida de la Activity
   - Logs de recepción y procesamiento de SMS
   - Manejo de excepciones con logging de errores

5. **Seguridad**
   - Validación de origen de SMS
   - Lista blanca de números autorizados
   - Receiver exportado con permisos del sistema

---

## 🔧 Tecnologías y Herramientas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Kotlin** | 1.9.0 | Lenguaje principal |
| **Android SDK** | API 35-36 | Framework Android |
| **Gradle** | 8.13.0 | Sistema de build |
| **Material Design** | 1.13.0 | Componentes UI |
| **AndroidX Core KTX** | 1.13.1 | Extensiones Kotlin |
| **JUnit** | 4.13.2 | Pruebas unitarias |
| **Espresso** | 3.7.0 | Pruebas UI |

---

## 📱 Requisitos del Sistema

### Requisitos Mínimos
- **Android**: 14 (API 35) o superior
- **RAM**: 2 GB mínimo
- **Permisos**: `RECEIVE_SMS`

### Requisitos de Desarrollo
- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 11 o superior
- **Gradle**: 8.13+
- **SDK Build Tools**: 34.0.0+

---

## 🛠️ Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/BRODCAST.git
cd BRODCAST
```

### 2. Abrir en Android Studio
1. Abre Android Studio
2. File → Open → Selecciona la carpeta del proyecto
3. Espera a que Gradle sincronice las dependencias

### 3. Configurar Emulador o Dispositivo
```bash
# Verificar dispositivos conectados
adb devices

# Si usas emulador, inicia uno desde AVD Manager
# Recomendado: Pixel 9 Pro XL con Android 16
```

### 4. Compilar e Instalar
```bash
# Compilar
./gradlew assembleDebug

# Instalar en dispositivo
./gradlew installDebug

# O directamente desde Android Studio: Run → Run 'app'
```

---

## 🎮 Uso de la Aplicación

### Primer Inicio

1. **Instala la app** en tu dispositivo o emulador
2. **Abre la aplicación** - verás una lista de números autorizados
3. **Otorga el permiso** de SMS cuando se solicite
4. **Envía un SMS de prueba** desde uno de los números autorizados

### Números Autorizados por Defecto

- `1234567890`
- `0987654321`

### Agregar Nuevos Números

Edita los siguientes archivos:

**MainActivity.kt** (línea 17):
```kotlin
private val phoneNumbers = listOf("1234567890", "0987654321", "TU_NUMERO")
```

**SmsReceiver.kt** (línea 12):
```kotlin
private val phoneNumbers = listOf("1234567890", "0987654321", "TU_NUMERO")
```

---

## 🧪 Pruebas

### Pruebas Unitarias
```bash
./gradlew test
```

### Pruebas Instrumentadas
```bash
./gradlew connectedAndroidTest
```

### Enviar SMS de Prueba (Emulador)

#### Método 1: Extended Controls
1. Abre el emulador
2. Click en "..." (More)
3. Phone → SMS
4. Ingresa número: `1234567890`
5. Escribe mensaje: "Prueba"
6. Click "Send Message"

#### Método 2: ADB Command
```bash
# SMS desde número autorizado (debe aparecer)
adb emu sms send 1234567890 "Hola desde SMS"

# SMS desde número NO autorizado (no debe aparecer)
adb emu sms send 9999999999 "Este no aparecerá"
```

### Verificar Logs
```bash
# Ver logs de MainActivity
adb logcat -s MainActivity:D

# Ver logs de SmsReceiver
adb logcat -s SmsReceiver:D

# Ver errores
adb logcat *:E

# Ver todos los logs de la app
adb logcat | grep -E "MainActivity|SmsReceiver|AndroidRuntime"
```

---

## 📊 Componentes Principales

### 1. MainActivity.kt

**Responsabilidades:**
- Inicialización de la UI
- Solicitud de permisos en tiempo de ejecución
- Mostrar lista de números autorizados
- Logging del ciclo de vida

**Métodos Clave:**
```kotlin
- onCreate(): Inicializa la Activity y solicita permisos
- checkAndRequestSmsPermission(): Verifica y solicita permiso SMS
- onRequestPermissionsResult(): Maneja respuesta de permisos
- onStart(), onResume(), onPause(), onDestroy(): Lifecycle logging
```

### 2. SmsReceiver.kt

**Responsabilidades:**
- Interceptar broadcasts de SMS_RECEIVED
- Extraer información del SMS (remitente, contenido)
- Filtrar por lista de números autorizados
- Mostrar notificación Toast

**Flujo de Ejecución:**
```
SMS entrante → Sistema Android → BroadcastReceiver.onReceive()
                                           ↓
                                  Extraer datos del Intent
                                           ↓
                                  Validar remitente en lista
                                           ↓
                                  Mostrar Toast si autorizado
```

### 3. AndroidManifest.xml

**Configuración:**
```xml
<!-- Permiso peligroso: requiere autorización del usuario -->
<uses-permission android:name="android.permission.RECEIVE_SMS" />

<!-- Activity principal -->
<activity android:name=".MainActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<!-- Broadcast Receiver para SMS -->
<receiver 
    android:name=".SmsReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION" />
    </intent-filter>
</receiver>
```

---

## 🐛 Debugging y Troubleshooting

### La App se Cierra al Iniciar

**Solución:** Revisa Logcat para ver el stacktrace
```bash
adb logcat *:E | grep AndroidRuntime
```

**Causa común:** Error en AndroidManifest.xml (ya corregido)

### No se Reciben SMS

**Verificaciones:**
1. ✓ Permiso RECEIVE_SMS otorgado
2. ✓ Número del remitente está en la lista autorizada
3. ✓ El emulador está recibiendo SMS (revisar notificaciones)
4. ✓ El Receiver está registrado en el manifest

**Debug:**
```bash
# Ver logs del receiver
adb logcat -s SmsReceiver:D

# Verificar que el receiver está registrado
adb shell dumpsys package com.example.brodcast | grep -A 10 "Receiver"
```

### Toast no Aparece

**Causa:** Toast requiere que la app esté en foreground o use un servicio

**Solución:** Implementar notificaciones en lugar de Toast para background:
```kotlin
val notificationManager = context.getSystemService(NotificationManager::class.java)
val notification = NotificationCompat.Builder(context, CHANNEL_ID)
    .setContentTitle("SMS de $sender")
    .setContentText(messageBody)
    .setSmallIcon(R.drawable.ic_sms)
    .build()
notificationManager.notify(notificationId, notification)
```

---

## 📈 Mejoras Futuras

### Funcionalidades Pendientes

- [ ] **Base de datos local** (Room) para almacenar SMS recibidos
- [ ] **Interfaz de gestión** de números autorizados (agregar/eliminar)
- [ ] **Notificaciones persistentes** en lugar de Toast
- [ ] **Sincronización con servidor** remoto
- [ ] **Cifrado de mensajes** sensibles
- [ ] **Filtrado por contenido** (palabras clave)
- [ ] **Respuestas automáticas** a SMS específicos
- [ ] **Estadísticas** de SMS recibidos
- [ ] **Widget de pantalla principal** con contador
- [ ] **Modo oscuro** completo
- [ ] **Exportar/Importar** lista de números
- [ ] **Integración con Wear OS**

### Optimizaciones Técnicas

- [ ] Migrar a **Jetpack Compose** para UI moderna
- [ ] Implementar **ViewModel** y **LiveData/Flow**
- [ ] Usar **Repository pattern** para abstracción de datos
- [ ] Implementar **Dependency Injection** con Hilt
- [ ] Agregar **pruebas unitarias** completas (>80% coverage)
- [ ] **CI/CD** con GitHub Actions
- [ ] **Ofuscación** con ProGuard/R8
- [ ] **Analytics** con Firebase
- [ ] **Crash reporting** con Crashlytics

---

## 🔐 Seguridad y Privacidad

### Permisos Utilizados

| Permiso | Nivel | Propósito |
|---------|-------|-----------|
| `RECEIVE_SMS` | Peligroso | Interceptar SMS entrantes |

### Buenas Prácticas Implementadas

✅ **Runtime Permissions**: Solicitud en tiempo de ejecución, no solo manifest
✅ **Validación de entrada**: Verificación de remitente antes de procesar
✅ **Logging seguro**: No se registran contenidos sensibles en producción
✅ **Principio de mínimo privilegio**: Solo se solicita el permiso necesario
✅ **Manejo de excepciones**: Try-catch para prevenir crashes

### Recomendaciones de Seguridad

1. **En producción, desactiva logs sensibles:**
```kotlin
if (!BuildConfig.DEBUG) {
    // No registrar información sensible
    return
}
```

2. **Implementa cifrado para datos sensibles:**
```kotlin
val encryptedPrefs = EncryptedSharedPreferences.create(...)
```

3. **Valida siempre el origen de los datos:**
```kotlin
if (!isValidPhoneNumber(sender)) {
    Log.w(TAG, "Invalid sender format")
    return
}
```

---

## 📚 Recursos y Referencias

### Documentación Oficial
- [Android Developers - Broadcasts](https://developer.android.com/guide/components/broadcasts)
- [Android Developers - Permissions](https://developer.android.com/guide/topics/permissions/overview)
- [Android Developers - SMS](https://developer.android.com/reference/android/telephony/SmsMessage)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

### Artículos y Tutoriales
- [Threading in Android](https://developer.android.com/guide/background)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Modern Android Development](https://developer.android.com/modern-android-development)

---

## 👨‍💻 Autor

**Tu Nombre**
- GitHub: [@tu-usuario](https://github.com/tu-usuario)
- Email: tu-email@example.com

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

```
MIT License

Copyright (c) 2025 [Tu Nombre]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Agradecimientos

- Comunidad de Android Developers
- Stack Overflow por las soluciones a problemas comunes
- Material Design para las guías de UI/UX
- JetBrains por Kotlin

---

## 📞 Soporte

Si encuentras algún bug o tienes sugerencias:

1. **Issues**: Abre un issue en [GitHub Issues](https://github.com/tu-usuario/BRODCAST/issues)
2. **Pull Requests**: Las contribuciones son bienvenidas
3. **Email**: Contacta a tu-email@example.com

---

## 📝 Changelog

### [1.0.0] - 2025-10-17

#### Added
- ✅ Implementación inicial de MainActivity
- ✅ SmsReceiver para interceptar SMS
- ✅ Sistema de permisos en tiempo de ejecución
- ✅ Logging detallado para debugging
- ✅ Manejo de excepciones
- ✅ Lista de números autorizados
- ✅ Notificaciones Toast
- ✅ Documentación completa

#### Fixed
- 🐛 Error crítico en AndroidManifest.xml (`android.icon` → `android:icon`)
- 🐛 Falta de soporte de Kotlin en el proyecto
- 🐛 Ausencia de callback para permisos

#### Changed
- 🔄 Migración de Java a Kotlin
- 🔄 Mejora del manejo de errores
- 🔄 Optimización de logs

---

**¡Gracias por usar BRODCAST!** 🚀📱

Si este proyecto te fue útil, no olvides darle una ⭐ en GitHub.





