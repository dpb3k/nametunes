package com.example.namethattune.components

import android.media.SoundPool
import android.content.Context
import com.example.namethattune.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val soundId: Int

    init {
        // Initialize SoundPool with max streams
        soundPool = SoundPool.Builder().setMaxStreams(1).build()
        // Load the sound from raw folder
        soundId = soundPool.load(context, R.raw.selection_sound, 1)
    }

    // Method to play the sound
    fun playSound() {
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    // Don't forget to release resources when done
    fun release() {
        soundPool.release()
    }
}
