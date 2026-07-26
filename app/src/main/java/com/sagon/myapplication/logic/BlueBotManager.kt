package com.sagon.myapplication.logic

data class BotResponse(
    val text: String,
    val productToSearch: String? = null,
    val isAlert: Boolean = false
)

object BlueBotManager {

    fun getResponse(query: String, weather: WeatherInfo? = null): BotResponse {
        val q = query.lowercase().trim()
        
        // CONSEJO PROACTIVO BASADO EN PREVISIÓN (Nuevo)
        if (weather != null && weather.maxTemps.size >= 2) {
            val tomorrow = weather.maxTemps[1]
            val today = weather.temp
            if (tomorrow >= 35.0 && (q.contains("tiempo") || q.contains("mañana") || q.contains("calor"))) {
                return BotResponse(
                    "¡Ojo! Mañana va a hacer un calor extremo (${tomorrow.toInt()}°C). Te recomiendo subir 2 o 3 horas el filtrado hoy mismo para que el agua no se corrompa.",
                    isAlert = true
                )
            }
        }

        return when {
            // COMPRAS Y REPARACIONES ESPECÍFICAS
            contains(q, listOf("gresite", "azulejo", "suelto", "pegar", "pasta", "porcelana", "negra", "llaga", "junta", "lechada")) -> 
                BotResponse(
                    "Para las juntas (llagas), lo mejor es usar **Lechada Impermeable** o Epoxi para que no se ponga negra. Si el gresite se ha caído, usa un adhesivo **MS Polymer** que pega bajo el agua.",
                    "Lechada piscina antimoho blanca"
                )
            
            contains(q, listOf("grieta", "fuga", "reparar", "perdiendo agua")) -> 
                BotResponse(
                    "Para grietas pequeñas, la masilla epoxi bicomponente es ideal. Se amasa y se aplica directamente sobre la grieta, endureciendo incluso sumergida.",
                    "Masilla epoxi piscinas"
                )

            contains(q, listOf("comprar", "producto", "necesito", "oferta", "catalogo", "precio")) && 
            contains(q, listOf("cloro", "ph", "algicida", "floculante", "pastillas")) -> {
                val search = when {
                    q.contains("cloro") -> "Cloro triple accion piscinas"
                    q.contains("ph") -> "Reductor e incrementador pH piscina"
                    q.contains("algicida") -> "Algicida concentrado piscinas"
                    q.contains("floculante") -> "Floculante en saquitos piscinas"
                    else -> "Mantenimiento piscinas"
                }
                BotResponse(
                    "He buscado el producto en los principales catálogos. Te recomiendo comparar entre estas tiendas para encontrar la mejor oferta de hoy.",
                    search
                )
            }

            contains(q, listOf("motor", "depuradora", "bomba")) && contains(q, listOf("comprar", "precio", "nuevo", "oferta")) ->
                BotResponse(
                    "Para motores nuevos, te busco las mejores opciones de 0.75 y 1 CV. Compara precios para ahorrar en la instalación.",
                    "Bomba depuradora piscina 0.75 CV"
                )

            contains(q, listOf("llave", "selector", "valvula", "cabezal")) -> 
                BotResponse(
                    "La válvula selectora es vital. Si necesitas un recambio, busca el modelo de 6 vías compatible con tu filtro.",
                    "Valvula selectora piscina 6 vias"
                )

            contains(q, listOf("limpiador", "fondo", "robot")) -> 
                BotResponse(
                    "Los robots a batería Wybot son los más populares ahora por calidad-precio. Mira las ofertas actuales en las distintas tiendas.",
                    "Robot limpiafondos piscina bateria"
                )

            // RESPUESTAS TÉCNICAS (CONSEJOS)
            contains(q, listOf("cloro alto", "bajar cloro")) -> 
                BotResponse("Quita las pastillas, destapa la piscina y deja que le dé el sol. Si tienes prisa, vacía un 10% de agua y rellena.")
            
            contains(q, listOf("verde", "algas")) -> 
                BotResponse(
                    "¡Alarma! Ajusta pH a 7.2, usa cloro de choque y cepilla bien. Te recomiendo un buen algicida de mantenimiento.",
                    "Algicida piscinas concentrado"
                )
            
            contains(q, listOf("turbia", "blanca", "lechosa")) -> 
                BotResponse(
                    "Falta filtración o hay cal. Usa un clarificador o floculante para que la suciedad baje al fondo y puedas aspirarla.",
                    "Floculante piscinas"
                )

            // Consultas sobre hardware/productos
            contains(q, listOf("wifi", "aparato", "dispositivo", "automatico")) -> 
                BotResponse(
                    "Te recomiendo los medidores WiFi como el 'Yieryi' o 'Blue Connect'. Puedes ver precios y stock en directo aquí.",
                    "Medidor inteligente piscina wifi"
                )

            // ESTACIONAL: INVIERNO / HIBERNAR
            contains(q, listOf("hibernar", "invernar", "invierno", "frio", "ivernacion")) -> 
                BotResponse(
                    "El invernador es un 'seguro' para tu agua. Existen dos tipos:\n\n1. **Boya**: Muy cómoda, se pincha y flota. Dura 2 meses.\n2. **Líquido**: Más preciso y económico, pero hay que diluirlo. Dura 3 meses.\n\nTe recomiendo limpiar bien el fondo y ajustar el pH a 7.2 antes de echarlo. ¡Compara precios aquí!",
                    "Producto invernador piscina liquido boya"
                )

            contains(q, listOf("boya", "bote", "flotante", "tetones")) -> 
                BotResponse(
                    "Las boyas de invernada duran ~2 meses. Perfora los tetones según tus 31m³ (unos 3-4 agujeros). ¡No la dejes pegada a la pared!",
                    "Boya invernada piscina 4 acciones"
                )

            contains(q, listOf("vaciar", "lona", "tapar", "cubierta")) -> 
                BotResponse(
                    "¡NO VACÍES la piscina! El sol y el frío sin agua rajan el gresite. Si no tienes lona, filtra 1h al día y usa invernador.",
                    "Lona cubierta piscina invierno"
                )

            contains(q, listOf("primavera", "preparar", "puesta a punto", "abrir")) -> 
                BotResponse(
                    "¡Hora de abrir! 1. Quita lona. 2. Limpia fondo en vaciado (no por filtro). 3. Ajusta pH. 4. Cloro choque. 5. Filtra 24h.",
                    "Kit puesta a punto piscina primavera"
                )
            
            contains(q, listOf("hola", "buenos dias", "quien eres", "saludos")) -> 
                BotResponse("¡Hola! Soy Blue Bot, tu experto en piscinas. ¿En qué puedo ayudarte hoy?")
            
            else -> BotResponse("No estoy seguro de entenderte. Prueba con: 'ofertas de cloro', 'reparar gresite' o 'robot limpiafondos'.")
        }
    }

    private fun contains(q: String, keywords: List<String>): Boolean {
        return keywords.any { q.contains(it) }
    }
}
