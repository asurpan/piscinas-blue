package com.sagon.myapplication.logic

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
        *AVISO LEGAL Y PRIVACIDAD*
        Responsable: Jose Manuel G.
        Cumplimos el RGPD: Tus datos se sincronizan de forma segura con Google Firebase.
        
        *DESCARGO DE RESPONSABILIDAD:*
        Esta app ofrece estimaciones basadas en algoritmos químicos. Lee siempre la etiqueta del fabricante. No nos hacemos responsables del uso indebido de productos.
    """.trimIndent()

    val CLOUD_HELP = """
        *TUS DATOS SIEMPRE A SALVO*
        Gracias a Google Firebase, tus datos se guardan automáticamente en la nube. 
        
        - Si cambias de móvil: Solo tienes que entrar con tu cuenta de Google y todo aparecerá como lo dejaste.
        - Compartir Piscina: Dale al botón de "+" en la cabecera para unirte a la piscina de otra persona o dales tu ID para que ellos se unan a la tuya. 
        - Tiempo Real: Lo que tú cambies, lo verán los demás al instante.
    """.trimIndent()
}
