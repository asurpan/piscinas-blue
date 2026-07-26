package com.sagon.piscinasblue.logic

object HelpContent {
    val PH_HELP = """
        *EL PH: EL ESTADO DE ÁNIMO DEL AGUA*
        El pH mide qué tan ácida o básica está el agua.
        Nivel ideal: Entre 7.2 y 7.6.
        - Si está ALTO (> 7.6): El agua se vuelve turbia y salen algas. Usa "Reductor de pH".
        - Si está BAJO (< 7.2): Puede irritar ojos y piel. Usa "Incrementador de pH".
    """.trimIndent()

    val CHLORINE_HELP = """
        *EL CLORO: EL DESINFECTANTE*
        Mata bacterias y mantiene el agua cristalina.
        Nivel ideal: Entre 1 y 3 ppm.
        - Pastillas: Mantenimiento diario.
        - Granulado: Limpieza rápida o "choque".
    """.trimIndent()

    val WINTER_HELP = """
        *INVERNAJE: DORMIR LA PISCINA*
        Para cuando el agua baja de los 15°C. 
        1. Limpia bien fondo y paredes. 2. Ajusta pH a 7.4. 3. Añade Invernador. 4. Pon la lona.
    """.trimIndent()

    val VOLUME_HELP = """
        *¿CÓMO CALCULAR LOS LITROS?*
        Largo x Ancho x Profundidad media x 1000 = Litros.
        (1 m³ son 1000 litros).
    """.trimIndent()

    val SAFETY_HELP = """
        *SEGURIDAD ELÉCTRICA*
        Una vez al mes, pulsa el botón "TEST" del cuadro de luces. La palanca debe saltar.
        - Si salta: Tu sistema es seguro.
        - Si NO salta: ¡Peligro! Llama a un electricista, el diferencial está roto.
    """.trimIndent()

    val OUTDOOR_HELP = """
        *LIMPIEZA EXTERIOR Y DESINFECCIÓN*
        - Piedra: Usa desincrustante y luego hidrofugante para que no se manche.
        - Desinfección: Usa lejía diluida (1%) para matar virus y hongos.
        - Truco Casero: Vinagre blanco para quitar la cal de los bordes.
        - ¡CUIDADO! Jamás mezcles lejía con vinagre. Es gas tóxico.
    """.trimIndent()

    val LEGAL_NOTICE = """
        *POLÍTICA DE PRIVACIDAD Y AVISO LEGAL*
        Responsable: Jose Manuel G.
        
        *1. DATOS QUE RECOGEMOS:*
        - Ubicación: Se solicita una única vez para configurar el clima de tu piscina. Se guarda en el dispositivo y en tu cuenta privada de Firebase.
        - Datos de Piscina: Mediciones de pH, Cloro y volumen.
        
        *2. FINALIDAD:*
        Cumplimos el RGPD. Tus datos se usan exclusivamente para personalizar los cálculos de mantenimiento y avisarte de olas de calor.
        
        *3. DERECHOS:*
        Puedes borrar todos tus datos y tu cuenta desde el botón "BORRAR CUENTA" en este menú.
        
        *4. DESCARGO DE RESPONSABILIDAD:*
        Esta app ofrece estimaciones. Lee siempre la etiqueta del fabricante químico.
    """.trimIndent()

    val CLOUD_HELP = """
        *TUS DATOS SIEMPRE A SALVO*
        Gracias a Google Firebase, tus datos se guardan automáticamente en la nube. 
        
        - Si cambias de móvil: Solo tienes que entrar con tu cuenta de Google y todo aparecerá como lo dejaste.
        - Compartir Piscina: Dale al botón de "+" en la cabecera para unirte a la piscina de otra persona o dales tu ID para que ellos se unan a la tuya. 
        - Tiempo Real: Lo que tú cambies, lo verán los demás al instante.
    """.trimIndent()

    val BRAIN_HELP = """
        *¿CÓMO FUNCIONA MI CEREBRO?*
        Soy una IA diseñada para aprender de tu piscina específica. Aquí tienes las claves:

        *1. Factor de Aprendizaje:*
        Es mi capacidad de mejora. Cada vez que cambias una pastilla, comparo mi predicción con la realidad. Si tu pastilla dura menos de lo previsto (por mucho uso o calor oculto), ajusto tu factor personal para avisarte antes la próxima vez.

        *2. Previsión Climática:*
        Recalculo la vida de los químicos según el clima local:
        - *Calor (>30-35°C):* Resto días de vida al cloro porque se evapora rápido.
        - *Viento (>20 km/h):* Aumento el consumo por evaporación y suciedad.

        *3. Parámetros Químicos:*
        - *Filtrado:* Si el pH es alto (>7.8), te pediré más horas de depuradora para estabilizar el agua.
        - *Alertas:* Si el cloro baja de 1.0, recibirás un aviso de salud inmediato, aunque a la pastilla le quede tiempo.

        *4. Modos Inteligentes:*
        - *Vacaciones:* Con el dosificador cerrado, multiplico la duración para evitar avisos falsos.
        - *Cantidad:* Sé que 2 pastillas no duran el doble que una, y aplico una curva de eficiencia real.
        
        *En resumen:* Cuanto más registres tus cambios, más preciso me vuelvo. ¡Aprendo de ti!
    """.trimIndent()
}
