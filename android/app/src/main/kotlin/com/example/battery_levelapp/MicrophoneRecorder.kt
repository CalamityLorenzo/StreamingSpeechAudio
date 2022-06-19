package com.example.battery_levelapp

import android.media.MediaRecorder
import android.os.ParcelFileDescriptor
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MicrophoneRecorder(inputStream: InputStream, fileDescriptor: ParcelFileDescriptor) {

    private var recorder: MediaRecorder
    private val inputStream = inputStream
    private val fileDesc = fileDescriptor
    private val byteArrayOutputStream:ByteArrayOutputStream =  ByteArrayOutputStream()

    init{
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileDesc.fileDescriptor)
            setAudioChannels(1)
            //setAudioEncodingBitRate()
        }
        recorder.prepare()
    }

    public fun Start(){
        recorder.start()
        var read=0;
        val data = ByteArray(16384)
        read = inputStream.read(data, 0, data.size)
        while (read != -1) {
            byteArrayOutputStream.write(data, 0, read);
            read = inputStream.read(data, 0, data.size)
        }
        byteArrayOutputStream.flush()
    }

    public fun Stop(){
        recorder.reset()
        recorder.release()
    }
}