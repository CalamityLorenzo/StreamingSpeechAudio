package com.example.battery_levelapp

import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback
import java.io.InputStream

class KotlinPullAudioStream(val inputStream: InputStream): PullAudioInputStreamCallback() {
    override fun read(p0: ByteArray?): Int {
        return inputStream.read(p0)
    }

    override fun close() {
        inputStream.close()
    }
}