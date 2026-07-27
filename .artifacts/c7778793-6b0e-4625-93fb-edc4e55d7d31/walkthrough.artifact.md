# Walkthrough - Guía visual para la configuración inicial

Se ha implementado un sistema de guiado visual para nuevos usuarios. Ahora, al instalar la app por primera vez, el campo de **Capacidad (m³)** parpadeará para indicar que es el primer paso necesario para configurar la piscina.

## Cambios realizados

### [PoolInputField.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/ui/components/PoolInputField.kt)
- Se ha añadido una **animación de pulso** infinita.
- Cuando el campo está en modo parpadeo (`isBlinking = true`), el borde y el fondo se iluminan rítmicamente con el color de acento.
- Se ha mejorado la sombra y el relieve durante el parpadeo para que destaque sobre el fondo.

### [Lógica de persistencia](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/data/PreferenceManager.kt)
- Se ha creado una nueva clave `HAS_SET_VOLUME` en `DataStore`.
- Esta clave rastrea si el usuario ya ha interactuado con la capacidad de su piscina.

### [PoolViewModel.kt](file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/ui/PoolViewModel.kt)
- El ViewModel ahora detecta cuando el volumen cambia respecto al valor por defecto.
- En cuanto se detecta un cambio, se marca la configuración como completada y el parpadeo se detiene para siempre en ese dispositivo.

## Verificación

> [!TIP]
> Puedes probarlo desinstalando la app y volviéndola a instalar. Verás que el campo azul de capacidad hace un efecto de "respiración". En cuanto escribas un número o uses la calculadora (icono de información), el efecto desaparecerá.

render_diffs(file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/ui/components/PoolInputField.kt)
render_diffs(file:///C:/Users/Jose/AndroidStudioProjects/PISCINAS BLUE/app/src/main/java/com/sagon/piscinasblue/ui/screens/DashboardScreen.kt)
