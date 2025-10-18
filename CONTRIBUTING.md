# Contribuyendo a BRODCAST

¡Gracias por tu interés en contribuir a BRODCAST! 🎉

## 🤝 Cómo Contribuir

### Reportar Bugs

Si encuentras un bug, por favor abre un [issue](https://github.com/RMJGLUCKY27/-BRODCAST---Aplicaci-n-de-Monitoreo-de-SMS/issues) con:

- **Descripción clara** del problema
- **Pasos para reproducir** el bug
- **Comportamiento esperado** vs comportamiento actual
- **Screenshots** si es aplicable
- **Versión de Android** y dispositivo
- **Logs relevantes** (de Logcat)

### Solicitar Funcionalidades

Para solicitar nuevas funcionalidades:

1. Verifica que no exista ya un issue similar
2. Abre un nuevo issue con la etiqueta "enhancement"
3. Describe claramente la funcionalidad deseada
4. Explica por qué sería útil
5. Proporciona ejemplos de uso si es posible

### Pull Requests

#### Proceso

1. **Fork** el repositorio
2. **Crea una rama** para tu funcionalidad:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. **Realiza tus cambios** siguiendo las guías de estilo
4. **Escribe pruebas** para tu código
5. **Asegúrate** de que todas las pruebas pasen:
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```
6. **Commit** tus cambios:
   ```bash
   git commit -m "Add: Nueva funcionalidad increíble"
   ```
7. **Push** a tu fork:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
8. Abre un **Pull Request** en GitHub

#### Guías de Estilo

**Código Kotlin:**
- Sigue las [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Usa nombres descriptivos para variables y funciones
- Mantén las funciones pequeñas y enfocadas
- Documenta funciones públicas con KDoc

**Commits:**
- Usa mensajes descriptivos en español
- Formato: `Tipo: Descripción breve`
- Tipos: `Add`, `Fix`, `Update`, `Remove`, `Refactor`, `Docs`, `Test`

Ejemplos:
```
Add: Implementar filtrado por palabras clave
Fix: Corregir crash al recibir SMS vacío
Update: Mejorar UI de la lista de números
Docs: Actualizar README con nueva funcionalidad
```

**Documentación:**
- Actualiza el README si es necesario
- Agrega comentarios para código complejo
- Documenta cambios en CHANGELOG.md

### Estructura de Branches

- `main`: Código estable en producción
- `develop`: Código en desarrollo (próxima versión)
- `feature/*`: Nuevas funcionalidades
- `bugfix/*`: Corrección de bugs
- `hotfix/*`: Correcciones urgentes

### Código de Conducta

Este proyecto sigue un código de conducta básico:

- ✅ Sé respetuoso y constructivo
- ✅ Acepta retroalimentación
- ✅ Enfócate en lo mejor para el proyecto
- ❌ No se toleran ataques personales
- ❌ No se permite spam o autopromoción

## 🧪 Ejecutar Pruebas

```bash
# Pruebas unitarias
./gradlew test

# Pruebas instrumentadas (requiere emulador/dispositivo)
./gradlew connectedAndroidTest

# Verificar estilo de código
./gradlew ktlintCheck

# Verificar build
./gradlew assembleDebug
```

## 📝 Checklist para Pull Requests

Antes de enviar un PR, verifica:

- [ ] El código compila sin errores
- [ ] Todas las pruebas pasan
- [ ] Se agregaron pruebas para nuevo código
- [ ] La documentación está actualizada
- [ ] No hay warnings de lint importantes
- [ ] Los commits tienen mensajes descriptivos
- [ ] Se actualizó el CHANGELOG.md

## 🎨 Mejoras Sugeridas

Si buscas algo en qué trabajar, aquí hay algunas ideas:

### Funcionalidades
- [ ] Base de datos Room para almacenar SMS
- [ ] Interfaz para gestionar números autorizados
- [ ] Notificaciones persistentes
- [ ] Respuestas automáticas
- [ ] Estadísticas de SMS recibidos
- [ ] Exportar/importar configuración
- [ ] Modo oscuro completo

### Mejoras Técnicas
- [ ] Migrar a Jetpack Compose
- [ ] Implementar MVVM con ViewModel
- [ ] Agregar Dependency Injection (Hilt)
- [ ] Aumentar cobertura de pruebas
- [ ] Optimizar rendimiento
- [ ] Mejorar accesibilidad

### Documentación
- [ ] Tutoriales en video
- [ ] Capturas de pantalla actualizadas
- [ ] Guía de arquitectura detallada
- [ ] Ejemplos de uso avanzado

## 🌐 Recursos Útiles

- [Android Developer Docs](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design Guidelines](https://material.io/design)
- [GitHub Flow](https://guides.github.com/introduction/flow/)

## 📞 Contacto

Si tienes preguntas sobre cómo contribuir:

- Abre un [Discussion](https://github.com/RMJGLUCKY27/-BRODCAST---Aplicaci-n-de-Monitoreo-de-SMS/discussions)
- Contacta al maintainer vía issues

---

**¡Gracias por hacer de BRODCAST un mejor proyecto!** 🚀
