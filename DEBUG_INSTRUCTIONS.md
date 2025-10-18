# Instrucciones de Debugging para BRODCAST

## Problema Corregido
La aplicación se cerraba al iniciar debido a un error tipográfico en `AndroidManifest.xml`:
- **Error**: `android.icon` (con punto)
- **Corrección**: `android:icon` (con dos puntos)

## Mejoras Implementadas

### 1. Logs de Debugging Agregados
Se agregaron logs extensivos en:
- `MainActivity.kt`: Logs del ciclo de vida (onCreate, onStart, onResume, onPause, onDestroy)
- `SmsReceiver.kt`: Logs de recepción y procesamiento de SMS
- Manejo de excepciones con try-catch en ambos archivos

### 2. Soporte de Kotlin
- Se agregó el plugin de Kotlin al proyecto
- Se agregó `core-ktx` para extensiones de Kotlin

### 3. Manejo de Permisos Mejorado
- Se implementó `onRequestPermissionsResult` para manejar la respuesta del usuario
- Se agregaron mensajes Toast informativos sobre el estado de permisos

## Cómo Verificar Logs en Logcat

### Opción 1: Desde Android Studio
1. Ejecuta la app en el emulador
2. Abre la ventana **Logcat** (View → Tool Windows → Logcat)
3. Filtra por:
   - **Tag**: `MainActivity` o `SmsReceiver`
   - **Package**: `com.example.brodcast`

### Opción 2: Desde PowerShell (ADB)
```powershell
# Ver logs en tiempo real de MainActivity
adb logcat -s MainActivity:D

# Ver logs en tiempo real de SmsReceiver
adb logcat -s SmsReceiver:D

# Ver todos los logs de la app
adb logcat | Select-String "MainActivity|SmsReceiver"

# Limpiar logs y ver solo los nuevos
adb logcat -c
adb logcat -s MainActivity:D SmsReceiver:D
```

## Comandos de Compilación

### Compilar y Instalar
```powershell
# Compilar APK de debug
.\gradlew assembleDebug

# Compilar e instalar en dispositivo/emulador
.\gradlew installDebug

# Compilar, instalar y ejecutar
.\gradlew installDebug
adb shell am start -n com.example.brodcast/.MainActivity
```

### Limpiar y Recompilar
```powershell
# Limpiar y compilar desde cero
.\gradlew clean assembleDebug
```

## Probar Recepción de SMS en Emulador

### Método 1: Desde Android Studio
1. Abre **Extended Controls** (ícono "..." en el emulador)
2. Ve a **Phone** → **SMS**
3. Ingresa un número de la lista permitida: `1234567890` o `0987654321`
4. Escribe un mensaje de prueba
5. Haz clic en **Send Message**

### Método 2: Desde PowerShell con ADB
```powershell
# Enviar SMS desde número permitido
adb emu sms send 1234567890 "Mensaje de prueba"

# Enviar SMS desde número no permitido (no debe mostrarse)
adb emu sms send 9999999999 "Este no debe aparecer"
```

## Logs Esperados

### Al Iniciar la App
```
D/MainActivity: onCreate - Iniciando aplicación
D/MainActivity: onCreate - Layout establecido correctamente
D/MainActivity: onCreate - ListView configurado con 2 números
D/MainActivity: Permiso RECEIVE_SMS no otorgado, solicitando...
D/MainActivity: onStart - Aplicación visible
D/MainActivity: onResume - Aplicación en primer plano
```

### Al Otorgar Permiso
```
D/MainActivity: Permiso RECEIVE_SMS otorgado por el usuario
```

### Al Recibir SMS
```
D/SmsReceiver: onReceive - Broadcast recibido: android.provider.Telephony.SMS_RECEIVED
D/SmsReceiver: onReceive - SMS_RECEIVED_ACTION detectado
D/SmsReceiver: onReceive - Número de mensajes: 1
D/SmsReceiver: onReceive - SMS de: 1234567890
D/SmsReceiver: onReceive - Mensaje: Mensaje de prueba
D/SmsReceiver: onReceive - Número autorizado, mostrando notificación
```

## Verificar Instalación

### Comprobar que la App está Instalada
```powershell
adb shell pm list packages | Select-String "brodcast"
```

### Ver Información de la App
```powershell
adb shell dumpsys package com.example.brodcast
```

### Desinstalar (si es necesario)
```powershell
adb uninstall com.example.brodcast
```

## Solución de Problemas Comunes

### La app se cierra inmediatamente
1. Verifica los logs de crash:
   ```powershell
   adb logcat *:E
   ```
2. Busca líneas con `AndroidRuntime` o `FATAL EXCEPTION`

### No aparece la solicitud de permisos
1. Verifica que el emulador tenga API 23 o superior
2. Revisa si el permiso ya fue otorgado previamente:
   ```powershell
   adb shell pm list permissions -g -d
   ```

### No se reciben SMS
1. Verifica que el permiso fue otorgado
2. Usa un número de la lista: `1234567890` o `0987654321`
3. Revisa los logs de `SmsReceiver`

### Error de compilación
1. Limpia y recompila:
   ```powershell
   .\gradlew clean build
   ```
2. Invalida cachés en Android Studio: File → Invalidate Caches / Restart

## Números de Teléfono Configurados
Los siguientes números están configurados para recibir notificaciones:
- `1234567890`
- `0987654321`

Para agregar más números, edita la lista en:
- `MainActivity.kt` línea 17
- `SmsReceiver.kt` línea 12

## Archivos Modificados
- ✅ `AndroidManifest.xml` - Corregido error de sintaxis
- ✅ `MainActivity.kt` - Agregados logs y manejo de errores
- ✅ `SmsReceiver.kt` - Agregados logs y manejo de errores
- ✅ `app/build.gradle.kts` - Agregado soporte de Kotlin
- ✅ `gradle/libs.versions.toml` - Agregadas dependencias de Kotlin
