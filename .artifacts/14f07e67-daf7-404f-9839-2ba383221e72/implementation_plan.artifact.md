# Plan de Super-Compactación (Ajuste Final)

El objetivo es terminar de ajustar la UI para que quepa holgadamente en una pantalla sin scroll, corrigiendo los elementos que se ven apretados en la captura del usuario.

## Cambios Propuestos

### 1. StatusIndicator.kt [MODIFY]
- Reducir el tamaño del emoji de `50.sp` a `40.sp`.
- Reducir el padding de la columna de `8.dp` a `4.dp`.

### 2. DashboardScreen.kt [MODIFY]
- Reducir la altura del botón "HE CAMBIADO LA PASTILLA" de `56.dp` a `48.dp`.
- Ajustar el tamaño de fuente del botón a `14.sp`.
- Reducir el espaciado superior de la sección invernal si aplica.
- Reducir el padding de la `ResultsSection` de `16.dp` a `12.dp`.

### 3. PoolInputField.kt [MODIFY]
- Reducir el tamaño de la fuente del label de `12.sp` a `11.sp`.
- Reducir el padding vertical del campo de `4.dp` a `2.dp`.

## Plan de Verificación

### Verificación Manual
- Desplegar en el dispositivo.
- Verificar que el botón de la pastilla se lea completamente.
- Confirmar que los créditos al pie de página no toquen la barra de navegación del sistema.
