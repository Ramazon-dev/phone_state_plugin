package com.example.phone_state

import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.example.phone_state.handler.FlutterHandler
import com.example.phone_state.utils.PhoneStateStatus

@RequiresApi(Build.VERSION_CODES.S)
class MyAudioManager() : AudioManager.OnModeChangedListener {
    companion object {
        var isActive = false
    }

    override fun onModeChanged(mode: Int) {
        when (mode) {
            AudioManager.MODE_IN_COMMUNICATION, AudioManager.MODE_IN_CALL -> {
                isActive = true
                invokeCall(PhoneStateStatus.CALL_STARTED.name)
            }
            else -> {
                if (mode == 0 && isActive) {
                    isActive = false
                    invokeCall(PhoneStateStatus.CALL_ENDED.name)
                }
            }
        }
    }

    private fun invokeCall(data: String) {
        Handler(Looper.getMainLooper()).post {
            synchronized(Any()) {
                FlutterHandler.phoneEvents?.success(
                    mapOf(
                        "status" to data, "phoneNumber" to null
                    )
                ) ?: println("invokeMethodUIThread: tried to call method on closed channel: $data")
            }
        }
    }
}

class MyAudioFocusChangeListener() : AudioManager.OnAudioFocusChangeListener {
    companion object {
        var isActive = false
    }
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.MODE_IN_COMMUNICATION, AudioManager.MODE_IN_CALL, AudioManager.MODE_INVALID -> {
                isActive = true
                invokeCall(PhoneStateStatus.CALL_STARTED.name)
            }
            else -> {
                if (focusChange == 1 && isActive) {
                    isActive = false
                    invokeCall(PhoneStateStatus.CALL_ENDED.name)
                }
            }
        }
    }

    private fun invokeCall(data: String) {
        Handler(Looper.getMainLooper()).post {
            synchronized(Any()) {
                FlutterHandler.phoneEvents?.success(
                    mapOf(
                        "status" to data, "phoneNumber" to null
                    )
                ) ?: println("invokeMethodUIThread: tried to call method on closed channel: $data")
            }
        }
    }
}
