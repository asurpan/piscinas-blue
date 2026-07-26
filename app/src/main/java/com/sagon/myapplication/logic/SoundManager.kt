package com.sagon.myapplication.logic

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.sagon.myapplication.R

object SoundManager {
    private var soundPool: SoundPool? = null
    private var clickSound: Int = 0
    private var successSound: Int = 0
    private var transitionSound: Int = 0

    fun init(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Cargamos sonidos del sistema por defecto para no pesar la app
        // En una app real usaríamos R.raw.click_sound
        clickSound = 1 // Placeholder
    }

    fun playClick() {
        soundPool?.play(clickSound, 0.5f, 0.5f, 1, 0, 1f)
    }
    
    // Al no tener archivos .wav en res/raw, usaremos el Beep del sistema o vibración
}
