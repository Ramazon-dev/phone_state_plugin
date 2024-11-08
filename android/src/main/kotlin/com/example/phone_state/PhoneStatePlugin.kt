package com.example.phone_state

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings
import android.os.Build
import com.example.phone_state.handler.FlutterHandler
import com.example.phone_state.utils.Constants
import com.example.phone_state.utils.PhoneStateStatus
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** PhoneStatePlugin */
class PhoneStatePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var flutterHandler: FlutterHandler
    private lateinit var phoneStateMethodChannel: MethodChannel
    private var currentActivity: Activity? = null
    private var phoneStateEventChannel: EventChannel? = null
    private var context: Context? = null


    private val audioManager by lazy {
        context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        phoneStateEventChannel = EventChannel(binding.binaryMessenger, Constants.EVENT_CHANNEL)
        flutterHandler = FlutterHandler(phoneStateEventChannel!!, audioManager)
        phoneStateMethodChannel = MethodChannel(binding.binaryMessenger, Constants.METHOD_CHANNEL)
        phoneStateMethodChannel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        phoneStateEventChannel?.setStreamHandler(null)
        flutterHandler.dispose()
    }


    private fun getCurrentStatus(): PhoneStateStatus {
        when (audioManager?.mode) {
            AudioManager.MODE_IN_COMMUNICATION, AudioManager.MODE_IN_CALL, AudioManager.MODE_INVALID -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MyAudioManager.isActive = true
                } else {
                    MyAudioFocusChangeListener.isActive = true
                }
                return PhoneStateStatus.CALL_STARTED
            }
            else -> PhoneStateStatus.NOTHING
        }
        return PhoneStateStatus.NOTHING
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            Constants.OPEN_APP_SETTING_METHOD -> openAppSettings(result)
            Constants.GET_STATUS_METHOD -> result.success(getCurrentStatus().name)
            else -> result.notImplemented()
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        currentActivity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        currentActivity = null
    }

    private fun openAppSettings(result: Result, asAnotherTask: Boolean = false) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        if (asAnotherTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        currentActivity?.let {
            intent.data = Uri.fromParts("package", it.packageName, null)
            it.startActivity(intent)
        }
        result.success(true)
    }
}