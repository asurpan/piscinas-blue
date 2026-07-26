# Inteligencia Hidráulica "Smart & Silent"

Se ha implementado un motor de cálculo profesional para la depuradora, basado en ingeniería hidráulica real y sincronización climática.

## Mejoras Realizadas

### 1. Cálculo de Ingeniería (No más "12h fijas")
Se ha sustituido la regla genérica por una fórmula de **Recirculación de Agua**:
- **Caudal por Motor**: La app ahora sabe que un motor de **1 CV** mueve **16 m³/h**, mientras que uno de **0.5 CV** mueve **10 m³/h**.
- **Ciclos de Limpieza (Vueltas)**: La IA determina cuántas veces debe pasar toda el agua por el filtro según el calor:
    - Fresco (<20°C): 1 vuelta.
    - Calor (28-32°C): 2 vueltas.
    - Ola de calor (>33°C): 3 vueltas.
- **Resultado**: Para tus 31 m³ con un motor de 1 CV, el tiempo bajará de 12h a unas **4-6h** reales, ahorrando muchísima luz.

### 2. Nuevo Selector de Motor (CV)
En la sección de "Capacidad", se ha añadido un selector elegante para indicar la potencia de tu bomba (**0.5, 0.75, 1.0 o 1.5 CV**). La app recordará este valor para todos sus cálculos.
- **Confirmación de Seguridad**: Al cambiar la potencia, la app pedirá confirmación para evitar cambios accidentales que afecten al filtrado.

### 3. Diálogo de "Lógica de Filtrado"
Al pulsar sobre el resultado de las horas de depuradora, la app abrirá una ventana explicando exactamente cómo ha llegado a ese número, detallando el volumen de tu piscina, el caudal de tu bomba y los ciclos necesarios por el clima actual.

### 4. Notificaciones Inteligentes (Ola de Calor)
Se ha configurado el sistema en segundo plano (**WorkManager**) para que:
- **No te moleste cada día**.
- **Te avise SOLO si hay peligro**: Si el termómetro sube de 33°C, te enviará una alerta: *"¡Alerta Calor! Hoy sube la temperatura, aumenta el filtrado a X horas para proteger el agua."*

---
### 5. Interfaz de Datos en Paralelo
Se ha optimizado la pantalla para que los campos de **pH** y **Cloro** aparezcan en la misma línea horizontal:
- **Colores Diferenciados**:
    - **Naranja** para el pH (Laboratorio).
    - **Cian** para el Cloro (Agua).
- **Números más Grandes**: Se ha aumentado la fuente a **20.sp** para que los valores sean mucho más legibles y cómodos de introducir.
- **Botón Compartir**: Ahora situado junto al botón de cambio de pastilla para un reporte rápido.

> [!TIP]
> **Pruébalo ahora**: Cambia entre 0.5 CV y 1.5 CV en la pantalla principal y verás cómo las horas se ajustan al instante de forma profesional.
