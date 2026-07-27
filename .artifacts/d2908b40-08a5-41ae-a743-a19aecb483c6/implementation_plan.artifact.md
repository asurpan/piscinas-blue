# Plan: Escudo Invisible (Inmunidad para Compradores)

Este plan implementa un sistema de "inmunidad" silencioso para que los usuarios que paguen por la app en la Play Store nunca vean el bloqueo de 30 usos, incluso si la app pasa a ser gratuita y activas el sistema de donaciones más tarde.

## User Review Required

> [!IMPORTANT]
> **Estrategia de Uso**: Para que esto funcione, mientras la app sea de pago en la Play Store, debes mantener el campo `isEnabled` en **falso** en tu consola de Firebase. Esto marcará automáticamente a todos los compradores como "Genuinos" para siempre.

## Proposed Changes

### 1. Persistencia de Estado
#### [MODIFY] [PreferenceManager.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/data/PreferenceManager.kt)
- Añadir `IS_GENUINE` como clave de DataStore.
- Crear función `setGenuine()` para guardar el estado permanentemente.

### 2. Lógica de Activación Silenciosa
#### [MODIFY] [PoolViewModel.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/ui/PoolViewModel.kt)
- Añadir el StateFlow `isGenuine`.
- Implementar la función `checkSystemIntegrity()` (nombre discreto). Esta función detectará si el sistema de "truco" está apagado globalmente y, en ese caso, otorgará inmunidad perpetua al dispositivo local.

### 3. Ajuste del Bloqueo
#### [MODIFY] [MainActivity.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/MainActivity.kt)
- Modificar la variable `limitReached` para que ignore el bloqueo si el usuario tiene la marca de inmunidad.

## Verification Plan
### Manual Verification
1. **Fase Pago**: Poner `isEnabled = false` en Firebase. Abrir la app. Verificar que no hay rastro de bloqueo.
2. **Fase Transición**: Poner `isEnabled = true` en Firebase. Los usuarios que abrieron la app en la Fase 1 NO deben ver el bloqueo aunque pasen de 30 usos.
3. **Fase Nuevos Usuarios**: Instalar la app de cero con `isEnabled = true`. Al llegar a 30 usos, debe saltar la pantalla de activación normalmente.
