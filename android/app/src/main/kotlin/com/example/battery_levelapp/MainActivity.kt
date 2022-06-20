package com.example.battery_levelapp

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.lang.reflect.Method
import java.util.logging.StreamHandler

class MainActivity : FlutterActivity() {

    private val SpeechChannelName = "samples.flutter.dev/speech";
    private val SpeechEventChannelName = "events.flutter.dev/speech";
    private val speechRecog: SpeechRecognition = SpeechRecognition()

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val speechChannel =
            MethodChannel(flutterEngine.dartExecutor.binaryMessenger, SpeechChannelName)
        speechChannel.setMethodCallHandler(speechRecog)
        val speechEventChannel = EventChannel(flutterEngine.dartExecutor.binaryMessenger, SpeechEventChannelName)
        speechEventChannel.setStreamHandler(speechRecog)

        }
    }



/*
   private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        } else {
            var intent = ContextWrapper(applicationContext).registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            batteryLevel =
                intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    -1
                )
        }
        return batteryLevel
    }
 */
