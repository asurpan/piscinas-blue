package com.sagon.piscinasblue.logic

data class BotResponse(
    val text: String,
    val productToSearch: String? = null,
    val isAlert: Boolean = false
)

object BlueBotManager {

    fun getResponse(query: String, weather: WeatherInfo? = null): BotResponse {
        val q = query.lowercase().trim()
        
        // CONSEJO PROACTIVO BASADO EN PREVISIÓN
        if (weather != null && weather.maxTemps.size >= 2) {
            val tomorrow = weather.maxTemps[1]
            if (tomorrow >= 35.0 && (q.contains("tiempo") || q.contains("mañana") || q.contains("calor"))) {
                return BotResponse(
                    "¡Ojo! Mañana va a hacer un calor extremo (${tomorrow.toInt()}°C). Te recomiendo subir 2 o 3 horas el filtrado hoy mismo para que el agua no se corrompa.",
                    isAlert = true
                )
            }
        }

        return when {
            // DETECCIÓN DE INTENCIÓN DE COMPRA / NECESIDAD (Frases naturales)
            contains(q, listOf("necesito", "comprar", "dónde hay", "precio", "quiero")) && 
            contains(q, listOf("cloro", "ph", "algicida", "floculante", "pastillas", "producto")) -> {
                val product = when {
                    q.contains("cloro") -> "Cloro triple accion piscinas"
                    q.contains("ph") -> "Reductor e incrementador pH piscina"
                    q.contains("algicida") -> "Algicida concentrado piscinas"
                    q.contains("floculante") -> "Floculante en saquitos piscinas"
                    else -> "Productos mantenimiento piscina"
                }
                BotResponse(
                    "He buscado el producto que necesitas en los catálogos actuales. Te recomiendo comparar entre estas tiendas para encontrar la mejor oferta de hoy.",
                    product
                )
            }

            // DETECCIÓN DE SÍNTOMAS / PROBLEMAS (Frases naturales)
            contains(q, listOf("problema", "tiene", "está", "veo")) && 
            contains(q, listOf("verde", "algas", "turbia", "sucia", "bichos", "blanca")) -> {
                val symptom = when {
                    q.contains("verde") || q.contains("algas") -> "verde"
                    q.contains("turbia") || q.contains("blanca") -> "turbia"
                    else -> "sucia"
                }
                when (symptom) {
                    "verde" -> BotResponse(
                        "¡Vaya! Si está verde es que tienes algas. 1. Ajusta pH a 7.2. 2. Usa cloro de choque. 3. Cepilla y filtra 24h.",
                        "Cloro de choque granulado piscina"
                    )
                    "turbia" -> BotResponse(
                        "El agua turbia suele ser falta de filtración o exceso de cal. Lava el filtro y usa un clarificador.",
                        "Floculante clarificador piscina"
                    )
                    else -> BotResponse("Parece que el agua necesita atención. ¿Te gustaría que buscara productos de limpieza?")
                }
            }

            // REPARACIONES
            contains(q, listOf("gresite", "azulejo", "suelto", "pegar", "pasta", "porcelana", "negra", "llaga", "junta", "lechada")) -> 
                BotResponse(
                    "Para las llagas, usa lechada impermeable o epoxi para que no se pongan negras. Si vas a pegar gresite bajo el agua, usa adhesivo MS Polymer.",
                    "Lechada piscina antimoho blanca"
                )
            
            contains(q, listOf("grieta", "fuga", "reparar", "perdiendo agua")) -> 
                BotResponse(
                    "Para grietas, la masilla epoxi bicomponente es ideal porque endurece incluso sumergida y es muy resistente.",
                    "Masilla epoxi piscinas"
                )

            // MOTORES Y SELECTORES
            contains(q, listOf("motor", "depuradora", "bomba")) && contains(q, listOf("comprar", "precio", "nuevo", "oferta")) ->
                BotResponse(
                    "Fíjate bien en los CV del motor para tu volumen de agua. Te busco las mejores opciones de 0.75 y 1 CV.",
                    "Bomba depuradora piscina 0.75 CV"
                )

            contains(q, listOf("llave", "selector", "valvula", "cabezal")) -> 
                BotResponse(
                    "¡Cuidado! Apaga siempre el motor antes de mover la válvula selectora. Si necesitas un recambio, busca el modelo de 6 vías.",
                    "Valvula selectora piscina 6 vias"
                )

            contains(q, listOf("limpiador", "fondo", "robot")) -> 
                BotResponse(
                    "Para el fondo, los robots Wybot a batería son imbatibles ahora mismo. Olvídate de los tubos y cables.",
                    "Robot limpiafondos piscina bateria"
                )

            // RESPUESTAS TÉCNICAS RÁPIDAS
            contains(q, listOf("cloro alto", "bajar cloro")) -> 
                BotResponse("Destapa la piscina y deja que le dé el sol para que el cloro se evapore. También puedes renovar un poco de agua.")
            
            contains(q, listOf("algicida", "antimoho")) -> 
                BotResponse("El algicida es preventivo. Si ya tienes algas, usa cloro de choque primero. ¿Quieres comprar algicida?", "Algicida concentrado piscinas")

            // GENERALES
            contains(q, listOf("wifi", "aparato", "dispositivo", "automatico")) -> 
                BotResponse(
                    "Los medidores WiFi como el 'Yieryi' te permiten ver el pH y Cloro desde el sofá. ¡Una maravilla!",
                    "Medidor inteligente piscina wifi"
                )
            
            contains(q, listOf("hola", "buenos dias", "quien eres", "saludos")) -> 
                BotResponse("¡Hola! Soy Blue Bot, tu experto en piscinas. ¿En qué puedo ayudarte hoy?")
            
            else -> BotResponse("No estoy seguro de entenderte. Prueba con: 'necesito algicida', 'mi piscina está verde' o 'robot limpiafondos'.")
        }
    }

    private fun contains(q: String, keywords: List<String>): Boolean {
        return keywords.any { q.contains(it) }
    }
}
