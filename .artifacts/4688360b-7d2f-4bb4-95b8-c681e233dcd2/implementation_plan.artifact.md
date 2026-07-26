# Plan de Implementación: PISCINAS BLUE "Ultimate Pro"

Este plan consolida todas las funciones solicitadas, asegurando que sean 100% funcionales, visibles y cómodas, añadiendo además la capacidad de hablar con la app y un registro histórico de mantenimiento.

## User Review Required

> [!IMPORTANT]
> **Visibilidad de Funciones**: Hemos detectado que las funciones de control estaban muy compactas. Vamos a expandir la interfaz para que cada herramienta (Clima, Bot, Seguridad, Historial, Ofertas) tenga su propio espacio protagonista.
> **Asistente por Voz**: Integraremos el motor de Google para que puedas dictar tus problemas.

## Proposed Changes

### [Módulo de Historial y Datos]

#### [NEW] [MaintenanceLogEntity.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/data/local/MaintenanceLogEntity.kt)
Tabla Room para guardar cada acción: "pH ajustado", "Cloro añadido", "Pastilla cambiada", "Limpieza exterior".

#### [NEW] [HistoryScreen.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/screens/HistoryScreen.kt)
Pantalla tipo línea de tiempo (Timeline) donde se ve el registro completo de la piscina.

### [Módulo de Voz]

#### [NEW] [SpeechRecognizerManager.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/logic/SpeechRecognizerManager.kt)
Integración con `SpeechRecognizer` de Android para permitir dictado en el chat.

### [Módulo de Compras (Funcional)]

#### [NEW] [OffersScreen.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/screens/OffersScreen.kt)
Pantalla real que muestra ofertas de cloro, pH y robots buscados en Amazon/Supermercados.

### [UI/UX - Rediseño de Confort]

#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/screens/DashboardScreen.kt)
- **Textos Gigantes**: Títulos a 32sp, números de valores a 28sp.
- **Iconos con Texto**: Cada botón de la cabecera tendrá una etiqueta debajo (Bot, Seguridad, Historial, Ofertas).
- **Acceso Directo a Depuradora**: Sección visual clara con las horas de filtrado recomendadas.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/MainActivity.kt)
- Añadir rutas de navegación para `history` y `offers`.

## Verification Plan

### Manual Verification
- Comprobar que al cambiar un valor se guarda una entrada en el historial.
- Probar el botón de micrófono en el chat.
- Verificar que en pantallas de 6 pulgadas los textos se leen sin esfuerzo.
