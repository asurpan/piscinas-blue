# Plan: Sustitución de Teclado por Selectores de Precisión (Steppers)

El objetivo es eliminar la fricción de tener que teclear valores decimales, sustituyendo los campos de texto tradicionales por un sistema de incremento/decremento mediante botones, manteniendo la opción de escritura manual para cambios drásticos.

## Proposed Changes

### 1. Componente `PoolInputField.kt` [MODIFY]
- Rediseñar el componente para que sea un **Stepper**.
- Añadir botones `[-]` y `[+]` táctiles en los extremos del control.
- El valor central será un `Text` (o un `BasicTextField` optimizado) que reacciona a los botones.
- **Parámetros nuevos**:
    - `step: Double`: La cantidad que aumenta/disminuye en cada pulsación (0.1 o 1.0).
    - `onIncrement`: Callback para sumar.
    - `onDecrement`: Callback para restar.

### 2. Pantalla `DashboardScreen.kt` [MODIFY]
- Actualizar las llamadas a `PoolInputField` para pasar los nuevos parámetros de incremento.
- Lógica de redondeo: Asegurar que al sumar 0.1 no aparezcan errores de precisión (ej: 7.200000001).

## Lógica de Pasos (Step)
- **Capacidad**: +/- 1.0 m³
- **pH**: +/- 0.1 unidades.
- **Cloro**: +/- 0.1 ppm.

## Verification Plan

### Manual Verification
1. Abrir la app.
2. Pulsar el `+` en el campo de pH -> Verificar que pasa de 7.2 a 7.3 instantáneamente sin abrir teclado.
3. Dejar pulsado el número -> Verificar que se puede escribir manualmente si se desea.
4. Comprobar que los botones son lo suficientemente grandes para usarse con comodidad.
