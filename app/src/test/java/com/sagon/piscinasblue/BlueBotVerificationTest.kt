package com.sagon.piscinasblue

import com.sagon.piscinasblue.logic.BlueBotManager
import com.sagon.piscinasblue.logic.PoolCalculator
import com.sagon.piscinasblue.data.PoolData
import org.junit.Test
import org.junit.Assert.*

class BlueBotVerificationTest {

    @Test
    fun verifyBotResponses() {
        val response1 = BlueBotManager.getResponse("tengo el cloro alto").lowercase()
        assertTrue("El bot debe sugerir quitar las pastillas", response1.contains("quita las pastillas"))

        val response2 = BlueBotManager.getResponse("mi piscina está verde").lowercase()
        assertTrue("El bot debe sugerir cloro de choque", response2.contains("cloro de choque"))

        val response3 = BlueBotManager.getResponse("depuradora luz").lowercase()
        assertTrue("El bot debe hablar de las horas baratas", response3.contains("00:00 a 08:00"))
    }

    @Test
    fun verifyCalculatorLogic() {
        val pool = PoolData(volumeM3 = 50.0, currentPh = 8.0, currentChlorine = 0.5)
        
        val score = PoolCalculator.getPoolScore(pool)
        assertTrue("Con pH 8 y cloro 0.5, la nota debe ser baja", score < 100)

        val clAdjustment = PoolCalculator.calculateChlorineAdjustment(pool)
        assertEquals("Para 50m3 y falta de 0.7ppm, necesita 350g", 350.0, clAdjustment, 0.1)
    }

    @Test
    fun verifyIntelligentLifespan() {
        val pool = PoolData(userConsumptionFactor = 1.0)
        
        val normalLife = PoolCalculator.calculateIntelligentTabletLifespan(pool, 25.0, 5.0)
        val heatLife = PoolCalculator.calculateIntelligentTabletLifespan(pool, 36.0, 5.0)
        
        assertTrue("Con calor la pastilla debe durar menos", heatLife < normalLife)
    }
}
