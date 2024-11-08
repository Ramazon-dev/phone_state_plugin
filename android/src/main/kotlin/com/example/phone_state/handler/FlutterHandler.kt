package com.example.phone_state.handler

import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.phone_state.MyAudioFocusChangeListener
import com.example.phone_state.MyAudioManager
import io.flutter.plugin.common.EventChannel
import java.util.concurrent.Executor

class FlutterHandler(
    phoneStateEventChannel: EventChannel,
    audioManager: AudioManager?,
) {
    var myAudioManager: MyAudioManager? = null
    var myAudioFocusChangeListener: MyAudioFocusChangeListener? = null
    private var localAudioManager: AudioManager? = null

    companion object {
        var phoneEvents: EventChannel.EventSink? = null
    }

    init {
        localAudioManager = audioManager
        phoneStateEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                phoneEvents = events
                if (myAudioManager == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    initMyAudioManager()
                } else if (myAudioManager == null && myAudioFocusChangeListener == null) {
                    initMyAudioFocusChangeListener()
                }
            }
            override fun onCancel(arguments: Any?) {}
        })
    }

    private val executor = Executor { r -> r.run() }

    @RequiresApi(Build.VERSION_CODES.S)
    fun initMyAudioManager() {
        println("initMyAudioManager")
        myAudioManager = MyAudioManager()
        localAudioManager?.addOnModeChangedListener(executor, myAudioManager!!)
    }

    fun initMyAudioFocusChangeListener() {
        println("initMyAudioManager2")
        myAudioFocusChangeListener = MyAudioFocusChangeListener()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localAudioManager?.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(myAudioFocusChangeListener!!).build()
            )
        } else {
            localAudioManager?.requestAudioFocus(
                myAudioFocusChangeListener,
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    fun dispose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && myAudioManager != null) {
            localAudioManager?.removeOnModeChangedListener(myAudioManager!!)
        }
    }
}
