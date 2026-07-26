package com.sagon.piscinasblue.logic

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

object SoundManager {
    private var toneGenerator: ToneGenerator? = null

    fun playClick() {
        if (toneGenerator == null) {
            toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 60)
        }
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
    }
}
