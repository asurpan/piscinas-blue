# Plan de Cambio de Nombre de Paquete

El objetivo es renombrar el identificador único de la aplicación de `com.sagon.myapplication` a `com.sagon.piscinasblue` para cumplir con los estándares de la Google Play Store y mejorar el profesionalismo del proyecto.

## User Review Required

> [!CAUTION]
> **Acción Requerida en Firebase**: Al cambiar el nombre del paquete, la conexión con Firebase (Firestore, Auth, etc.) dejará de funcionar inmediatamente.
> 1. Debes ir a tu [Consola de Firebase](https://console.firebase.google.com/).
> 2. Añadir una nueva aplicación de Android con el nombre `com.sagon.piscinasblue`.
> 3. Descargar el nuevo archivo `google-services.json`.
> 4. Sustituir el archivo actual en la carpeta `app/` de tu proyecto.

## Proposed Changes

### 1. Configuración de Compilación
#### [MODIFY] [build.gradle.kts](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/build.gradle.kts)
- Cambiar `namespace` a `"com.sagon.piscinasblue"`.
- Cambiar `applicationId` a `"com.sagon.piscinasblue"`.

### 2. Refactorización de Código Fuente
- Actualizar todas las declaraciones de `package` en los archivos Kotlin.
- Actualizar todos los `import` que apunten al paquete antiguo.
- Ajustar las referencias en `AndroidManifest.xml` (si las hay).

### 3. Ajuste de Estructura de Directorios
- Mover físicamente los archivos de la carpeta `com/sagon/myapplication/` a `com/sagon/piscinasblue/` para mantener la consistencia del proyecto.

## Plan de Verificación

### Pruebas Automatizadas
- Ejecutar `gradle_build` para asegurar que el proyecto compila con el nuevo nombre.

### Verificación Manual
- Abrir la App en el dispositivo.
- Verificar que el inicio de sesión de Google (que depende del nombre del paquete) funcione tras actualizar el `google-services.json` y el SHA-1 en la consola.
