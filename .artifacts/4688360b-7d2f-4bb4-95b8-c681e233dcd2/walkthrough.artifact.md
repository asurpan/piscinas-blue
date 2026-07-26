# Walkthrough: App Piscina Perfecta

He completado la implementación de la aplicación "Piscina Perfecta", una herramienta visual e intuitiva para el mantenimiento de piscinas tanto en verano como en invierno.

## Cambios Realizados

### 🏗️ Arquitectura por Módulos
He organizado el código siguiendo tu petición de módulos separados para facilitar el mantenimiento:
- **`data`**: Modelo [PoolData.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/data/PoolData.kt) que centraliza el estado de la piscina.
- **`logic`**: Lógica de cálculo en [PoolCalculator.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/logic/PoolCalculator.kt) para pH, Cloro e Invernaje.
- **`ui.components`**: Componentes reutilizables como [WaterFillAnimation.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/components/WaterFillAnimation.kt), [PoolInputField.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/components/PoolInputField.kt) y [ExitDialog.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/components/ExitDialog.kt).
- **`ui.screens`**: La pantalla principal [DashboardScreen.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/screens/DashboardScreen.kt).

### ✨ Características Destacadas
- **Splash Animado**: Una animación de 3 segundos donde la piscina se llena de agua azul.
- **Sin Scroll**: La interfaz está diseñada para caber perfectamente en una pantalla estándar usando tu imagen `p.png`.
- **Modo Invierno**: Un selector que cambia los cálculos de Verano (Cloro/pH) a Invierno (Invernador/Choque).
- **Confirmación de Salida**: Al pulsar atrás, aparece un diálogo para evitar cierres accidentales.

## Verificación

- **Compilación**: El proyecto compila correctamente con `compileSdk 37`.
- **Lógica**: Se han validado las fórmulas de dosificación:
    - Verano: 10g cloro/m3 por cada ppm faltante.
    - Invierno: 0.75L invernador por cada 10m3.
- **UI**: Textos grandes en español y controles optimizados para entrada numérica rápida.

> [!TIP]
> Puedes probar el cambio de modo pulsando el interruptor superior. Verás como los campos de pH y Cloro desaparecen en modo Invierno para centrarse en los litros de invernador necesarios.
