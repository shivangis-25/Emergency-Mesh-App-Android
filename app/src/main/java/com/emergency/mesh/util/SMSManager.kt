package com.emergency.mesh.util

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.emergency.mesh.data.EmergencyContact

class SMSManager(private val context: Context) {

    private val TAG = "SMSManager_UNIVERSAL"

    fun sendEmergencyAlert(contacts: List<EmergencyContact>, message: String): Result<Unit> {
        return try {
            if (contacts.isEmpty()) {
                Toast.makeText(context, "No emergency contacts saved", Toast.LENGTH_LONG).show()
                return Result.failure(Exception("No contacts"))
            }

            val smsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            val canSendDirect = smsPermission == PackageManager.PERMISSION_GRANTED

            Log.d(TAG, "Android version: ${Build.VERSION.SDK_INT}")
            Log.d(TAG, "Permission granted: $canSendDirect")

            if (canSendDirect && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                sendDirectSms(contacts, message)
            } else {
                openSmsAppFallback(contacts, message)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ SMS sending failed: ${e.message}", e)
            Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            Result.failure(e)
        }
    }

    private fun sendDirectSms(contacts: List<EmergencyContact>, message: String) {
        val smsManager = SmsManager.getDefault()

        val sentPI = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_SENT"),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val deliveredPI = PendingIntent.getBroadcast(
            context, 0, Intent("SMS_DELIVERED"),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Log.d(TAG, "✅ SMS sent successfully")
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Log.e(TAG, "❌ Generic failure")
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Log.e(TAG, "❌ No service")
                    SmsManager.RESULT_ERROR_NULL_PDU -> Log.e(TAG, "❌ Null PDU")
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Log.e(TAG, "❌ Radio off")
                    else -> Log.e(TAG, "❌ Unknown send error: $resultCode")
                }
            }
        }, IntentFilter("SMS_SENT"))

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Log.d(TAG, "📬 SMS delivered successfully!")
                    Activity.RESULT_CANCELED -> Log.e(TAG, "📭 SMS not delivered")
                }
            }
        }, IntentFilter("SMS_DELIVERED"))

        contacts.forEach { contact ->
            try {
                Log.d(TAG, "Sending to ${contact.name}: ${contact.phoneNumber}")
                smsManager.sendTextMessage(contact.phoneNumber, null, message, sentPI, deliveredPI)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to send to ${contact.phoneNumber}: ${e.message}", e)
            }
        }

        Toast.makeText(context, "✅ SMS sent successfully to all contacts", Toast.LENGTH_SHORT).show()
    }

    private fun openSmsAppFallback(contacts: List<EmergencyContact>, message: String) {
        try {
            val phoneNumbers = contacts.joinToString(",") { it.phoneNumber }
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumbers")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(smsIntent)
            Toast.makeText(context, "📱 Opening SMS app to send manually", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Fallback failed: ${e.message}", e)
            Toast.makeText(context, "Failed to open SMS app", Toast.LENGTH_LONG).show()
        }
    }
}
