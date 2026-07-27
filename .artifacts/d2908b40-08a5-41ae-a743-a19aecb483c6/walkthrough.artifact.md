# Caminata: Ubicación Inteligente y Modo Infierno (>40°C)

Se ha implementado un sistema completo de ubicación dinámica y una lógica de mantenimiento adaptada a temperaturas extremas, cumpliendo con los requisitos de Play Store.

## Cambios Realizados

### 1. Ubicación Dinámica e Inteligente
- **Detección Eficiente**: Se ha creado [LocationHelper.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/logic/LocationHelper.kt) para obtener la ubicación una sola vez y guardarla.
- **Persistencia**: Las coordenadas se guardan en DataStore a través de [PreferenceManager.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/data/PreferenceManager.kt).
- **Gestión de Usuario**: Se ha añadido un panel de ubicación en [SettingsDialog.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/ui/components/SettingsDialog.kt) que permite ver la ciudad actual y actualizarla manualmente.

### 2. Lógica "Modo Infierno" (>40°C)
- **PoolCalculator.kt**:
    - A los 40°C, el filtrado sube a **3.5 ciclos** (máximo).
    - El desgaste de la pastilla se penaliza con **3 días menos** de duración total para evitar que el agua se estropee.
- **MaintenanceWorker.kt**: Añadida una alerta específica de "Peligro de Evaporación" si la temperatura supera los 40°C.

### 3. Cumplimiento Play Store y Privacidad
- **Permisos**: [MainActivity.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/MainActivity.kt) ahora solicita los permisos de ubicación y notificaciones al inicio de forma correcta.
- **Transparencia**: Se ha actualizado la Política de Privacidad en [HelpContent.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/logic/HelpContent.kt) detallando por qué y cómo se usa la ubicación.

## Verificación Realizada
- Se ha comprobado que el sistema usa Sevilla (37.38, -5.98) como valor por defecto si el usuario no otorga permisos.
- La lógica de filtrado y pastillas ha sido actualizada para ser más agresiva con el calor extremo.

### Actualización: Escudo Invisible
Se ha implementado una protección silenciosa para compradores legales.
- **Detección Pasiva**: Los usuarios que descarguen la app mientras el "truco" está apagado en Firebase ganarán inmunidad perpetua.
- **Seguridad**: No se utilizan APIs de facturación de Google, lo que hace el código indistinguible de una configuración normal de la app.
- **Control Remoto**: El desarrollador decide cuándo activar el sistema de donaciones para nuevos usuarios sin afectar a los antiguos.
