package com.example.bebeka.logik

import android.content.Context
import android.media.MediaRecorder
import android.os.Bundle
import java.io.File
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

// Source - https://stackoverflow.com/q
// Posted by Katakam Nikhil, modified by community. See post 'Timeline' for change history
// Retrieved 2025-11-27, License - CC BY-SA 3.0

class SpeechRecognitionListener(
    private val onResultCallback: (String) -> Unit = {},
) : RecognitionListener {



    override fun onBeginningOfSpeech() {
        Log.d("SpeechRecognition", "Начало речи")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d("SpeechRecognition", "буфер")
    }

    override fun onEndOfSpeech() {
        Log.d("SpeechRecognition", "Конец записи")
    }

    override fun onError(error: Int) {
        Log.e("SpeechRecognition", "Ошибка: $error")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d("SpeechRecognition", "event")
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.wtf("SpeechRecognition", "хз это как вообще")
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d("SpeechRecognition", "Готов к речи")
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val bestMatch = matches?.get(0) // Наиболее вероятный результат
        if (!bestMatch.isNullOrEmpty()) {
            Log.d("SpeechRecognition", "Результат: $bestMatch")
            onResultCallback(bestMatch)
        } else {
            Log.d("SpeechRecognition", "Пустой результат")
        }
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d("SpeechRecognition", "Громкость?")
    }

}