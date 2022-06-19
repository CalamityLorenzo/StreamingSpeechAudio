package com.example.battery_levelapp

import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import android.util.Log
import com.microsoft.cognitiveservices.speech.*
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall


class SpeechRecognition : EventChannel.StreamHandler, MethodChannel.MethodCallHandler {

    private var speechConfig: SpeechConfig
    private var eventSink: EventChannel.EventSink? = null
    private var isSessionStarted = false
    private val subscriptionKey: String = "7e5fdf9fa44440d99940bf7d0af30d55"
    private val region: String = "UKSouth"
    private lateinit var recognizer: SpeechRecognizer
    private var isRecognizerSet = false
    private var microphoneRecorder: MicrophoneRecorder? = null;

    private lateinit var audioRecorder: AudoRecordRecorder;

    init {
        speechConfig = SpeechConfig.fromSubscription(subscriptionKey, region)
        speechConfig.speechRecognitionLanguage = "en-GB"
        // recognizerFromMicrophone(speechConfig)
        // recognizerEvents()
    }

    fun recognizerFromMicrophone(speechConfig: SpeechConfig) {
        val audioConfig = AudioConfig.fromDefaultMicrophoneInput()
        recognizer = SpeechRecognizer(speechConfig, audioConfig)
    }

    private fun recognizerEvents() {
        recognizer.sessionStarted.addEventListener { _: Any, _: SessionEventArgs -> sessionStartedEvent() }
        recognizer.sessionStopped.addEventListener { _: Any, e: SessionEventArgs ->
            sessionEndedEvent(
                e
            )
        }
        recognizer.canceled.addEventListener { _: Any, e: SpeechRecognitionCanceledEventArgs ->
            speechCancelled(
                e
            )
        }
        recognizer.recognizing.addEventListener { _: Any, e: SpeechRecognitionEventArgs ->
            speechRecognising(
                e
            )
        }

        recognizer.recognized.addEventListener { _: Any, e: SpeechRecognitionEventArgs ->
            speechFinalRecognised(
                e
            )
        }
    }

    private fun speechRecognising(e: SpeechRecognitionEventArgs) {
        Log.d("Speech", "Recongizing")
        eventSink?.let {
            Handler(Looper.getMainLooper()).post {
                // Call the desired channel message here.
                eventSink!!.success("Recognizing: ${e.result.text}");
            }
        }
    }

    private fun speechFinalRecognised(e: SpeechRecognitionEventArgs) {
        Log.d("Speech", "Final Recognised")
        eventSink?.let {
            Handler(Looper.getMainLooper()).post {
                eventSink!!.success("Recognised: ${e.result.text}");
            }
        }
    }

    private fun speechCancelled(e: SpeechRecognitionCanceledEventArgs) {
        Log.d("Speech", "${e.errorCode}  ${e.errorDetails}");
        Log.d("Speech", "Canceled")
        eventSink?.let {
            Handler(Looper.getMainLooper()).post {
                eventSink!!.success("SessionCancelled ${e.errorCode}  ${e.errorDetails}")
            }
        }
    }

    private fun sessionStartedEvent() {
        Log.d("Speech", "Session Started")
        eventSink?.let {
            Handler(Looper.getMainLooper()).post {
                // Call the desired channel message here.
                eventSink!!.success("SessionStarted Event")
            }
        }
    }

    private fun sessionEndedEvent(e: SessionEventArgs) {
        Log.d("Speech", "Session Ended")
        eventSink?.let {
            Handler(Looper.getMainLooper()).post {
                // Call the desired channel message here.
                eventSink!!.success("SessionEnded Event")
            }
        }
    }

    internal fun startSession() {
        eventSink?.let {
            Handler(Looper.getMainLooper()).post {
                // Call the desired channel message here.
                eventSink!!.success("SessionStart Called")
            }

        }
        recognizer.startContinuousRecognitionAsync()
    }

    internal fun sessionEnded() {
        eventSink?.let {
            eventSink!!.success("SessionEnded")
        }
        recognizer.stopContinuousRecognitionAsync()
    }

    internal fun startStreamSession() {
        eventSink?.let {
            eventSink!!.success("StreamSessionStart Event")
        }
        val descriptors = ParcelFileDescriptor.createPipe()
        val parcelRead = ParcelFileDescriptor(descriptors[0])
        var parcelWrite = ParcelFileDescriptor(descriptors[1])
        var inputStream = ParcelFileDescriptor.AutoCloseInputStream(parcelRead)


        var pushStream =
            AudioInputStream.createPushStream(AudioStreamFormat.getWaveFormatPCM(48000, 16, 1))
        val audioConfig = AudioConfig.fromStreamInput(pushStream)
            recognizer = SpeechRecognizer(speechConfig, audioConfig)
            recognizerEvents()

        isRecognizerSet = true
        audioRecorder = AudoRecordRecorder(pushStream)
        audioRecorder.Start()
        recognizer.startContinuousRecognitionAsync()

    }

    internal fun endStreamSession() {
        eventSink?.let {
            eventSink!!.success("StreamSessionEnded Event")
        }
        audioRecorder!!.Stop()
        recognizer.stopContinuousRecognitionAsync();
//        recognizer.close()

    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        this.eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null;
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        var out = when (call.method) {
            "startSession" -> startSession()
            "endSession" -> sessionEnded()
            "startStreamSession" -> startStreamSession()
            "endStreamSession" -> endStreamSession()
            else -> result.notImplemented()
        }

        result.success("Success")
    }


}