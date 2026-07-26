# Plan: Inteligencia Hidráulica "Smart & Silent"

El objetivo es implementar un cálculo de filtrado profesional basado en la potencia del motor (CV) que se actualice al abrir la app, evitando notificaciones diarias molestas y avisando solo ante cambios climáticos críticos (olas de calor).

## User Review Required

> [!IMPORTANT]
> - **Sin spam diario**: Solo habrá notificaciones si la temperatura sube drásticamente (>33°C) o si hay mantenimiento pendiente.
> - **Dato del Motor**: El usuario deberá elegir su bomba (CV) una sola vez para activar el ahorro energético.

## Proposed Changes

### 1. Modelos de Datos y Configuración
#### [MODIFY] [PoolData.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/data/PoolData.kt) y [PoolEntity.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/data/local/PoolEntity.kt)
- Añadir `pumpHp: Double = 0.75` (Potencia del motor).

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/data/local/AppDatabase.kt)
- Incrementar versión a `4`.

### 2. Cerebro de la Aplicación (IA)
#### [MODIFY] [PoolCalculator.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/logic/PoolCalculator.kt)
- **Caudal Real**: Mapear CV a m³/h (ej: 1CV = 16m³/h).
- **Cálculo de Ciclos**: Determinar cuántas veces debe pasar todo el volumen por el filtro según el calor del día.
- **Detección de Ola de Calor**: Lógica para comparar la temperatura de hoy con la media y decidir si enviar alerta.

### 3. Interfaz de Usuario (UI)
#### [MODIFY] [DashboardScreen.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/ui/screens/DashboardScreen.kt)
- **Selector de Motor**: Un pequeño selector de botones (0.5, 0.75, 1.0, 1.5) integrado en la zona de capacidad.
- **Diálogo Técnico**: Al pulsar sobre las horas de depuradora, la app explicará:
    - *"Tu piscina de 31m³ con motor de 1 CV mueve 16m³/h. Hoy, a 35°C, recomendamos 2 vueltas de agua (6 horas) para máxima desinfección."*

### 4. Background Inteligente (WorkManager)
#### [MODIFY] [MaintenanceWorker.kt](file:///C:/Users/Jose/AndroidStudioProjects/MyApplication/app/src/main/java/com/sagon/myapplication/logic/MaintenanceWorker.kt)
- Cambiar la notificación diaria por una **notificación condicional**: Solo avisa si el pronóstico de hoy supera los 33°C y el usuario no ha entrado en la app aún.

## Verification Plan

### Manual Verification
1. Abrir la app y cambiar el motor de 0.5 CV a 1.5 CV -> Verificar que las horas de depuradora bajan proporcionalmente.
2. Comprobar que al pulsar en el resultado aparece la explicación técnica detallada.
3. Verificar que el diseño sigue siendo compacto y sin scroll en el Xiaomi.
