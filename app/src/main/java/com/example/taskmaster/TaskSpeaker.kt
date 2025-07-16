package com.example.taskmaster

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TaskSpeaker(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady: Boolean = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        isReady = status != TextToSpeech.ERROR
        if (isReady) {
            tts?.language = Locale.getDefault()
        }
    }

    fun speak(text: String) {
        if (isReady && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        isReady = false
    }
}