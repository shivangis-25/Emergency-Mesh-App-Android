package com.emergency.mesh.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.emergency.mesh.data.EmergencyContact

class SMSManager(private val context: Context) {

    fun sendEmergencyAlert(contacts: List<EmergencyContact>, message: String): Result<Unit> {
        return try {
            if (!hasPermission()) {
                return Result.failure(SecurityException("SMS permission not granted"))
            }

            val smsManager = SmsManager.getDefault()

            contacts.forEach { contact ->
                smsManager.sendTextMessage(
                    contact.phoneNumber,
                    null,
                    message,
                    null,
                    null
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkSMSPermission(): Boolean {
        return hasPermission()
    }
}