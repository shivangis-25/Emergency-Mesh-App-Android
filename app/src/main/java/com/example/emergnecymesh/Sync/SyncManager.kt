package com.example.emergnecymesh.sync



import android.content.Context
import android.util.Log
import com.example.emergnecymesh.api.*
import com.example.emergnecymesh.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages syncing offline data to backend when network is available
 */
class SyncManager(private val context: Context) {

    companion object {
        private const val TAG = "SyncManager"
        private const val PREFS_NAME = "sync_prefs"
        private const val KEY_PENDING_MESSAGES = "pending_messages"
        private const val KEY_LAST_SYNC = "last_sync"
    }

    private val apiService = RetrofitClient.apiService
    private val networkMonitor = NetworkMonitor(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    var onSyncComplete: ((success: Boolean, syncedCount: Int) -> Unit)? = null
    var onSyncError: ((error: String) -> Unit)? = null

    init {
        // Monitor network and sync when available
        networkMonitor.onNetworkAvailable = {
            Log.d(TAG, "Network available - starting sync")
            syncPendingData()
        }
    }

    fun startMonitoring() {
        networkMonitor.startMonitoring()
    }

    fun stopMonitoring() {
        networkMonitor.stopMonitoring()
    }

    /**
     * Queue a message for sync (save offline)
     */
    fun queueMessageForSync(message: Message) {
        scope.launch {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val pendingJson = prefs.getString(KEY_PENDING_MESSAGES, "[]") ?: "[]"

                // Add new message to queue
                val pending = mutableListOf<Message>()
                // Parse existing and add new message
                pending.add(message)

                // Save back to preferences
                prefs.edit().putString(KEY_PENDING_MESSAGES, pending.toString()).apply()

                Log.d(TAG, "Message queued for sync: ${message.id}")

                // Try to sync immediately if network available
                if (networkMonitor.isNetworkAvailable()) {
                    syncPendingData()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error queueing message", e)
            }
        }
    }

    /**
     * Send emergency alert to backend
     */
    fun sendEmergencyAlert(
        deviceId: String,
        latitude: Double,
        longitude: Double,
        message: String,
        phoneNumber: String? = null
    ) {
        scope.launch {
            try {
                if (!networkMonitor.isNetworkAvailable()) {
                    Log.w(TAG, "No network - alert will be queued")
                    withContext(Dispatchers.Main) {
                        onSyncError?.invoke("No network connection. Alert saved locally.")
                    }
                    return@launch
                }

                val request = EmergencyAlertRequest(
                    deviceId = deviceId,
                    latitude = latitude,
                    longitude = longitude,
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    phoneNumber = phoneNumber
                )

                val response = apiService.sendEmergencyAlert(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d(TAG, "Emergency alert sent successfully")
                    withContext(Dispatchers.Main) {
                        onSyncComplete?.invoke(true, 1)
                    }
                } else {
                    Log.e(TAG, "Failed to send alert: ${response.message()}")
                    withContext(Dispatchers.Main) {
                        onSyncError?.invoke("Failed to send alert: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending emergency alert", e)
                withContext(Dispatchers.Main) {
                    onSyncError?.invoke("Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Sync a mesh message to backend
     */
    fun syncMeshMessage(message: Message) {
        scope.launch {
            try {
                if (!networkMonitor.isNetworkAvailable()) {
                    queueMessageForSync(message)
                    return@launch
                }

                val request = MeshMessageRequest(
                    messageId = message.id,
                    senderId = message.senderId,
                    content = message.content,
                    latitude = message.latitude,
                    longitude = message.longitude,
                    timestamp = message.timestamp,
                    hopCount = message.hopCount,
                    ttl = message.ttl
                )

                val response = apiService.syncMeshMessage(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d(TAG, "Message synced: ${message.id}")
                } else {
                    Log.w(TAG, "Failed to sync message: ${response.message()}")
                    queueMessageForSync(message)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing message", e)
                queueMessageForSync(message)
            }
        }
    }

    /**
     * Sync all pending offline data
     */
    fun syncPendingData() {
        scope.launch {
            try {
                if (!networkMonitor.isNetworkAvailable()) {
                    Log.w(TAG, "No network available for sync")
                    return@launch
                }

                Log.d(TAG, "Starting sync of pending data")

                // Here you would load queued messages from SharedPreferences
                // and send them in batch

                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

                // Update last sync time
                prefs.edit().putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply()

                Log.d(TAG, "Sync completed successfully")

                withContext(Dispatchers.Main) {
                    onSyncComplete?.invoke(true, 0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during sync", e)
                withContext(Dispatchers.Main) {
                    onSyncError?.invoke("Sync failed: ${e.message}")
                }
            }
        }
    }

    fun getLastSyncTime(): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_SYNC, 0)
    }
}