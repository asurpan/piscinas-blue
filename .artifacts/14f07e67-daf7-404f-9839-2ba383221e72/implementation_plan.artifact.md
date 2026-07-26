# Plan de Compactación de la Interfaz (Sin Scroll)

El objetivo es ajustar todos los elementos de la pantalla principal para que sean visibles sin necesidad de hacer scroll, optimizando el espacio vertical.

## Cambios Propuestos

### 1. Pantalla Principal (`DashboardScreen.kt`)
- Eliminar `Modifier.verticalScroll`.
- Reducir los espacios (`Spacer`) entre secciones.
- Ajustar el tamaño del botón de "Cambio de pastilla" para que sea menos alto.
- Reducir el padding de `ResultsSection`.

### 2. Indicador de Estado (`StatusIndicator.kt`)
- Reducir el tamaño del emoji de `70.sp` a `50.sp`.
- Reducir los paddings verticales para ahorrar espacio.

### 3. Campos de Entrada (`PoolInputField.kt`)
- Reducir la altura de los campos de `56.dp` a `48.dp`.
- Ajustar los paddings internos para mantener la proporción.

## Plan de Verificación

### Verificación Manual
- Desplegar en el dispositivo (Xiaomi).
- Comprobar que todos los elementos (Cabecera, Selector de Modo, Emoji, Inputs y Resultados) son visibles simultáneamente.
- Verificar que el teclado no oculte elementos críticos al activarse (usando `adjustResize` ya configurado en el Manifest).
