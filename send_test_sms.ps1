# Script para Enviar SMS de Prueba
# Ejecuta este script para enviar SMS de prueba al emulador

Write-Host "=== Enviando SMS de Prueba ===" -ForegroundColor Green
Write-Host ""

Write-Host "Opción 1: Enviar SMS desde número AUTORIZADO (1234567890)" -ForegroundColor Cyan
Write-Host "   Este SMS debería mostrarse en la aplicación" -ForegroundColor Gray
Write-Host ""
$opcion1 = Read-Host "¿Enviar SMS de prueba desde 1234567890? (S/N)"

if ($opcion1 -eq "S" -or $opcion1 -eq "s") {
    $mensaje = Read-Host "Ingresa el mensaje de prueba"
    adb emu sms send 1234567890 "$mensaje"
    Write-Host "✓ SMS enviado desde 1234567890: $mensaje" -ForegroundColor Green
    Write-Host ""
}

Write-Host ""
Write-Host "Opción 2: Enviar SMS desde número NO AUTORIZADO (9999999999)" -ForegroundColor Cyan
Write-Host "   Este SMS NO debería mostrarse en la aplicación" -ForegroundColor Gray
Write-Host ""
$opcion2 = Read-Host "¿Enviar SMS de prueba desde 9999999999? (S/N)"

if ($opcion2 -eq "S" -or $opcion2 -eq "s") {
    $mensaje2 = Read-Host "Ingresa el mensaje de prueba"
    adb emu sms send 9999999999 "$mensaje2"
    Write-Host "✓ SMS enviado desde 9999999999: $mensaje2" -ForegroundColor Green
    Write-Host "   (Este no debería aparecer en la app)" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host ""
Write-Host "=== Prueba Completa ===" -ForegroundColor Green
Write-Host "Verifica la aplicación en el emulador para ver si aparecen las notificaciones Toast." -ForegroundColor White
Write-Host "También puedes ver los logs con: adb logcat -s SmsReceiver:D" -ForegroundColor Gray
