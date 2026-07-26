package com.sagon.myapplication.logic

object BlueBotManager {

    fun getResponse(query: String): String {
        val q = query.lowercase()
        return when {
            contains(q, listOf("cloro alto", "bajar cloro", "demasiado cloro")) -> 
                "Quita las pastillas, destapa la piscina y deja que le dé el sol. Si tienes prisa, vacía un 10% de agua y rellena."
            contains(q, listOf("ph alto", "bajar ph", "alcalina")) -> 
                "El agua está alcalina. Usa un 'Reductor de pH'. Si tienes cascadas, apágalas un tiempo para que baje el nivel."
            contains(q, listOf("verde", "algas")) -> 
                "¡Alarma! 1. Ajusta pH a 7.2. 2. Cloro de choque (granulado). 3. Cepilla paredes. 4. Filtra 24 horas seguidas."
            contains(q, listOf("turbia", "blanca", "lechosa")) -> 
                "Falta filtración o hay cal. Lava el filtro de arena y usa 'Floculante' o 'Clarificador' para que la suciedad baje al fondo."
            contains(q, listOf("limpiar fuera", "bordes", "piedra", "desinfectar")) -> 
                "Usa lejía diluida al 1% para virus. Si solo es cal en la piedra, el vinagre blanco funciona de maravilla."
            contains(q, listOf("depuradora", "filtrar", "reloj", "horas")) -> 
                "En España, programa el 70% de 00:00 a 08:00 (luz barata). El resto a mediodía para mover el cloro."
            contains(q, listOf("wifi", "aparato", "dispositivo", "automatico")) -> 
                "Te recomiendo el 'Yieryi 7-in-1 WiFi' en AliExpress o el 'Blue Connect' en Amazon. Miden todo solos."
            contains(q, listOf("limpiador", "fondo", "barato", "robot")) -> 
                "Lo más barato es el limpiafondos Venturi (20€). Si buscas comodidad, el robot Wybot a batería es el mejor calidad-precio."
            contains(q, listOf("perro", "gato", "mascota", "seguro")) -> 
                "Usa repelentes naturales (neem) para insectos. Si usas químicos, asegúrate de que se sequen antes de dejar pasar a tu mascota."
            contains(q, listOf("hola", "buenos dias", "quien eres")) -> 
                "¡Hola! Soy Blue Bot, tu experto en piscinas. ¿En qué puedo ayudarte hoy?"
            else -> "No estoy seguro de entenderte. Prueba con: 'agua verde', 'cloro alto' o 'cómo programar depuradora'."
        }
    }

    private fun contains(q: String, keywords: List<String>): Boolean {
        return keywords.any { q.contains(it) }
    }
}
