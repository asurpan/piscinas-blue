# Consola de Control de Precisión (Steppers con Sonido)

Se ha eliminado la necesidad de teclear valores decimales, sustituyendo los campos tradicionales por selectores de precisión mucho más rápidos y profesionales.

## Cambios Realizados

### 1. Selectores de Precisión (Steppers) 🔘
Se han integrado botones `[-]` y `[+]` en los campos de **Capacidad**, **pH** y **Cloro**:
- **pH y Cloro**: Los botones aumentan o disminuyen el valor de **0.1 en 0.1**. Esto permite un ajuste fino rapidísimo sin abrir el teclado.
- **Capacidad**: Los botones ajustan el volumen de **1.0 en 1.0 m³**.
- **Escritura Manual**: Si necesitas hacer un cambio brusco, aún puedes pulsar sobre el número central para usar el teclado.

### 2. Feedback Auditivo (Beep) 🔊
Cada vez que pulsas un botón de incremento o decremento, la app emite un **sonido de "beep"** del sistema.
- Proporciona una confirmación inmediata de que la pulsación ha sido registrada, ideal para el uso en exteriores bajo el sol.

### 3. Redondeo Inteligente
Se ha implementado una lógica de redondeo matemático para evitar errores de precisión de punto flotante (como ver un "7.40000001"). Ahora los números siempre se verán limpios y profesionales.

## Cómo probarlo
1. Ve al Dashboard.
2. Pulsa suavemente el botón `+` del pH. Escucharás el **beep** y verás cómo sube una décima instantáneamente.
3. Prueba a dejar pulsado el número si quieres teclear un valor muy diferente.

---
> [!TIP]
> Este sistema es ideal cuando tienes las manos húmedas o hay mucha luz solar, ya que los botones son grandes y ofrecen respuesta sonora.
