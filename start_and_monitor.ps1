# Script para Iniciar y Monitorear BRODCAST
# Ejecuta este script para iniciar la app y ver los logs en tiempo real

Write-Host "=== Iniciando BRODCAST ===" -ForegroundColor Green
Write-Host ""

# Iniciar la aplicación
Write-Host "1. Iniciando la aplicación en el emulador..." -ForegroundColor Cyan
adb shell am start -n com.example.brodcast/.MainActivity

Start-Sleep -Seconds 2

Write-Host ""
Write-Host "2. La aplicación debería estar abierta ahora." -ForegroundColor Green
Write-Host ""
Write-Host "=== Monitoreando Logs (Ctrl+C para detener) ===" -ForegroundColor Yellow
Write-Host ""

# Limpiar logs anteriores y mostrar solo los nuevos
adb logcat -c
adb logcat -s MainActivity:D SmsReceiver:D AndroidRuntime:E
