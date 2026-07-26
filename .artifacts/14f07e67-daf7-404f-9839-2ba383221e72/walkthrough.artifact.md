# Añadido Efecto de Sonido de Piscina

Se ha integrado el sonido de agua para la pantalla de carga.

### 1. Implementación en `WaterFillAnimation`
Se ha modificado [WaterFillAnimation.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/components/WaterFillAnimation.kt) para usar `MediaPlayer`.
- El sonido comienza automáticamente al iniciar la animación.
- Se libera la memoria correctamente al finalizar o cerrar la pantalla.

### 2. Archivo de Sonido
He creado un archivo marcador de posición (placeholder) en:
- `app/src/main/res/raw/piscina_entrada.mp3`

### 3. Recuperación del Icono del Robot (Asistente)
Se ha cambiado el icono del asistente en el Dashboard de `SmartToy` a `Android`. Esto soluciona el problema de que el botón no apareciera en algunos dispositivos, asegurando que el acceso al asistente sea siempre visible.

> [!TIP]
> Actualmente el archivo es un marcador vacío para que la app compile. Para que suene de verdad, **sustituye el archivo `piscina_entrada.mp3`** en esa carpeta por uno que tenga el sonido de agua que prefieras.

---

# Solución al Inicio de Sesión en Android 15/16

Se han realizado cambios críticos para asegurar que el selector de cuentas de Google aparezca correctamente en las versiones más recientes de Android.

## Cambios Realizados

### 1. Librerías Actualizadas
Se han actualizado las dependencias de Credenciales en el archivo [libs.versions.toml](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/gradle/libs.versions.toml):
- `androidx.credentials`: de `1.3.0` a `1.6.0` (Versión estable más reciente).
- `com.google.android.libraries.identity.googleid`: de `1.1.1` a `1.2.0`.

### 2. Migración a `GetSignInWithGoogleOption`
En [AuthManager.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/logic/AuthManager.kt), se ha sustituido `GetGoogleIdOption` por `GetSignInWithGoogleOption`.
- **Por qué:** `GetGoogleIdOption` tiene un fallo documentado en Android 14+ que impide mostrar el diálogo si hay varias cuentas. La nueva opción es la recomendada para flujos de "Un toque" iniciados por botón.

### 3. Sincronización de Proyecto
Se ha ejecutado un Gradle Sync para aplicar los cambios y asegurar que la aplicación compile con las nuevas APIs.

## Cómo verificar
1. Ejecuta la aplicación en tu dispositivo con Android 15/16.
2. Ve a la pantalla de Onboarding y pulsa **"ENTRAR CON GOOGLE"**.
3. Ahora debería aparecer el selector de cuentas de Google en la parte inferior.

> [!IMPORTANT]
> Si después de esto sigue "dando vueltas" sin mostrar nada, es muy probable que el **SHA-1** de tu certificado de depuración no esté registrado en la consola de Firebase.
> Puedes obtenerlo con el comando: `./gradlew signingReport` en la terminal.
