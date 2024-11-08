package com.example.phone_state.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.phone_state.utils.PhoneStateStatus


open class PhoneStateReceiver : BroadcastReceiver() {
    var status: PhoneStateStatus = PhoneStateStatus.NOTHING
    var phoneNumber: String? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            status = when (intent?.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                TelephonyManager.EXTRA_STATE_RINGING -> PhoneStateStatus.CALL_INCOMING
                TelephonyManager.EXTRA_STATE_OFFHOOK -> PhoneStateStatus.CALL_STARTED
                TelephonyManager.EXTRA_STATE_IDLE -> PhoneStateStatus.CALL_ENDED
                else -> PhoneStateStatus.NOTHING
            }

            phoneNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
