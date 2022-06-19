package com.example.battery_levelapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.microsoft.cognitiveservices.speech.audio.PushAudioInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

@SuppressLint("MissingPermission")
class AudoRecordRecorder(pushStream: PushAudioInputStream) {

    lateinit var microphone: AudioRecord;
    private val bos: ByteArrayOutputStream = ByteArrayOutputStream()
    private val dataOutputStream: DataOutputStream = DataOutputStream(bos)
    private val pushS = pushStream
    var isStopped: Boolean = true

    init {

        val sampleRate = 48000;
        val channelConfig = AudioFormat.CHANNEL_IN_MONO;
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        microphone = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize * 10
        )
    }

    @SuppressLint("MissingPermission")
    public fun Start() {
        isStopped = false

        microphone.startRecording();

        //Since audioformat is 16 bit, we need to create a 16 bit (short data type) buffer
        val buffer: ShortArray = ShortArray(1024)

        Thread {
            while (!isStopped) {
                try {
                    var readSize = microphone.read(buffer, 0, buffer.size);
                    val byteByf: ByteBuffer = ByteBuffer.allocate(readSize * 2)
                    byteByf.order(ByteOrder.LITTLE_ENDIAN)
                    for (p in 0 until (buffer.size - 1)) {
                        byteByf.putShort(buffer[p])
                    }
                    pushS.write(byteByf.array())
                }
                catch (ex:BufferOverflowException){
                    Log.d("AutioRecorder", ex.message!!.toString())
                }
            }
            microphone.stop()
            microphone.release()
//            pushS.close()
        }.start()
    }

    public fun Stop() {
        isStopped = true
    }
}