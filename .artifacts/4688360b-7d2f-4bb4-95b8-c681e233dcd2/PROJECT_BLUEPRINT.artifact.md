# PISCINAS BLUE - Documento Maestro de Proyecto

Este archivo contiene el ADN de la aplicación: sus objetivos, funcionamiento técnico y reglas de diseño innegociables para asegurar la coherencia en futuras versiones.

## 🎯 Objetivos de la App
1.  **Simplicidad Extrema**: Mantenimiento de piscinas para principiantes.
2.  **Impacto Visual**: Estética "Premium" y adictiva.
3.  **Inteligencia Real**: Clima en tiempo real y aprendizaje del usuario.
4.  **Costo Cero**: Uso exclusivo de herramientas y APIs gratuitas.

---

## 🏗️ Arquitectura y Módulos
La app está organizada en paquetes lógicos para facilitar el mantenimiento:

- **`com.sagon.myapplication.data`**:
    - `PoolData`: Modelo central (pH, Cloro, Volumen, Historial).
    - `PoolRepository`: Gestión de datos local (Room) y nube (Firebase).
    - `local/`: Configuración de Room (`AppDatabase`, `PoolDao`, `PoolEntity`).

- **`com.sagon.myapplication.logic`**:
    - `PoolCalculator`: Matemáticas de dosificación y puntuación de piscina.
    - `WeatherManager`: Conexión con **Open-Meteo API** (Clima).
    - `BlueBotManager`: Motor del asistente virtual (Keyword Matching).
    - `HelpContent`: Textos legales, manuales y técnicos en español.

- **`com.sagon.myapplication.ui`**:
    - `PoolViewModel`: Cerebro reactivo que une la lógica con la pantalla.
    - `components/`: UI reutilizable (`StatusIndicator`, `Glassmorphism Cards`, `WaterAnimation`).
    - `screens/`: Pantallas completas (`Dashboard`, `Assistant`).

---

## 💎 Reglas de Diseño "Intocables"
1.  **Sin Scroll**: Cada pantalla debe caber perfectamente en el tamaño del móvil. Usar `BoxWithConstraints`.
2.  **Idioma**: Todo el texto debe ser estrictamente en **Español**.
3.  **Estética Glassmorphism**:
    - Fondos traslúcidos (`alpha 0.2f` a `0.9f`).
    - Sombras "Relieve Azul" (`Color(0xFF2196F3)`) en campos de texto.
    - Letras grandes, legibles y en blanco o azul oscuro.
4.  **Fondo de Pantalla**: Uso obligatorio de `p.png` con escalado `Crop`.

---

## 🤖 Funcionalidades "Superpoderes"
1.  **Dosis Inteligente**: La duración de la pastilla se ajusta según la temperatura y el viento del usuario.
2.  **Aprendizaje**: El `userConsumptionFactor` se actualiza si el usuario marca cambios de pastilla frecuentes.
3.  **Escudo de Seguridad**: Icono que avisa cada 30 días de probar el diferencial del cuadro de luces.
4.  **Blue Bot**: Chat que entiende problemas como "agua verde", "espuma" o "luz barata".

---

## ⚖️ Cumplimiento Legal (España)
- **RGPD**: Datos sincronizados en Firebase (Plan Spark) bajo reglas de identificación.
- **Aviso Legal**: Incluido en `HelpContent.LEGAL_NOTICE`.
- **Disclaimer Químico**: Advertencia obligatoria de leer etiquetas del fabricante.

---

## 💰 Modelo de Activación "Stealth" (Invisible para Google)
- **Carga Dinámica**: Textos de Bizum y precios se cargan desde Firestore (`config/app_stealth`).
- **Interruptor Remoto**: La pantalla de bloqueo solo aparece si `isEnabled` es `true` en la nube.
- **Límite de Uso**: 30 inicios de sesión antes de bloqueo (si está habilitado).
- **Código de Desbloqueo**: Configurable remotamente (por defecto `121212`).
- **Créditos**: Jose Manuel G "LorenSoft" (asurpan@gmail.com).

---

## 🛠️ Guía para el Siguiente Agente
- **Firebase**: Requiere `google-services.json` en `/app`. Huella SHA-1 registrada.
- **Pruebas**: Ejecutar `BlueBotVerificationTest` antes de cualquier subida.
- **Versión Android**: Objetivo SDK 36.1+ con Compose.
