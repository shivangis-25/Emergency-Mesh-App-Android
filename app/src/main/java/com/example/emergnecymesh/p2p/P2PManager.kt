package com.example.emergnecymesh.p2p

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.emergnecymesh.model.Message
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

/**
 * P2PManager handles peer-to-peer communication using Google Nearby Connections API.
 * Implements advertising, discovery, and message sending/receiving with proper hop count handling.
 */
class P2PManager(
    private val context: Context,
    providedDeviceId: String? = null
) : P2PSender {

    companion object {
        private const val TAG = "P2PManager"
        private const val SERVICE_ID = "com.emergency.mesh.p2p"
        private val STRATEGY = Strategy.P2P_CLUSTER

        /**
         * Generate a unique device ID
         */
        fun generateUniqueDeviceId(): String {
            val deviceModel = Build.MODEL.replace(" ", "_")
            val uniqueId = UUID.randomUUID().toString().take(8)
            return "${deviceModel}_${uniqueId}"
        }

        /**
         * Format timestamp to a user-readable string
         */
        fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    private val deviceId: String = providedDeviceId ?: generateUniqueDeviceId()

    var listener: ((Message) -> Unit)? = null
    var connectionStateListener: ((isConnected: Boolean, peerCount: Int) -> Unit)? = null
    var statusListener: ((status: String) -> Unit)? = null

    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)
    private val connectedEndpoints = mutableSetOf<String>()
    private var isAdvertising = false
    private var isDiscovering = false

    init {
        Log.d(TAG, "P2PManager initialized with deviceId: ${this.deviceId}")
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "Connection initiated with: ${info.endpointName} (ID: $endpointId)")
            statusListener?.invoke("Connecting to ${info.endpointName}...")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "Connected successfully to: $endpointId")
                    connectedEndpoints.add(endpointId)
                    statusListener?.invoke("Connected to peer! Total peers: ${connectedEndpoints.size}")
                    connectionStateListener?.invoke(true, connectedEndpoints.size)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.w(TAG, "Connection rejected by: $endpointId")
                    statusListener?.invoke("Connection rejected by peer")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.e(TAG, "Connection error with: $endpointId")
                    statusListener?.invoke("Connection error occurred")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Disconnected from: $endpointId")
            connectedEndpoints.remove(endpointId)
            statusListener?.invoke("Peer disconnected. Total peers: ${connectedEndpoints.size}")
            connectionStateListener?.invoke(connectedEndpoints.isNotEmpty(), connectedEndpoints.size)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val bytes = payload.asBytes()
                if (bytes != null) {
                    try {
                        val jsonString = String(bytes, StandardCharsets.UTF_8)
                        val message = parseMessage(jsonString)
                        val readableTime = formatTimestamp(message.timestamp)
                        Log.d(TAG, "Message received at $readableTime from $endpointId: ${message.content}")

                        // Notify listener
                        listener?.invoke(message)

                        // Forward message to other peers (avoid sending back to sender)
                        forwardMessage(message, excludeEndpoint = endpointId)

                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing received message", e)
                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> {
                    Log.d(TAG, "Payload sent successfully to: $endpointId")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    Log.e(TAG, "Payload failed to send to: $endpointId")
                }
                else -> {}
            }
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "Endpoint found: ${info.endpointName} (ID: $endpointId)")
            statusListener?.invoke("Peer found: ${info.endpointName}")
            connectionsClient.requestConnection(deviceId, endpointId, connectionLifecycleCallback)
                .addOnSuccessListener { Log.d(TAG, "Connection request sent to: $endpointId") }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to request connection to: $endpointId", e)
                    statusListener?.invoke("Failed to connect to peer")
                }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "Endpoint lost: $endpointId")
            statusListener?.invoke("Peer lost: $endpointId")
        }
    }

    fun checkPermissions(): PermissionStatus {
        val missingPermissions = mutableListOf<String>()
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE)) missingPermissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) missingPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) missingPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPermission("android.permission.NEARBY_WIFI_DEVICES")) missingPermissions.add("android.permission.NEARBY_WIFI_DEVICES")
        }
        return PermissionStatus(missingPermissions.isEmpty(), missingPermissions)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun isReadyForP2P(): ReadinessStatus {
        val permissionStatus = checkPermissions()
        val locationEnabled = isLocationEnabled()
        val issues = mutableListOf<String>()
        if (!permissionStatus.allGranted) issues.add("Missing permissions: ${permissionStatus.missingPermissions.joinToString(", ")}")
        if (!locationEnabled) issues.add("Location services are disabled")
        return ReadinessStatus(permissionStatus.allGranted && locationEnabled, issues)
    }

    fun startP2P(): Boolean {
        val readiness = isReadyForP2P()
        if (!readiness.ready) {
            Log.e(TAG, "Device not ready for P2P: ${readiness.issues}")
            val errorMessage = readiness.issues.firstOrNull() ?: "Unknown error"
            statusListener?.invoke("Cannot start P2P: $errorMessage")
            return false
        }
        startAdvertising()
        startDiscovery()
        return true
    }

    fun startAdvertising() {
        if (isAdvertising) {
            Log.w(TAG, "Already advertising")
            return
        }
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(deviceId, SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
            .addOnSuccessListener {
                isAdvertising = true
                Log.d(TAG, "Advertising started successfully as: $deviceId")
                statusListener?.invoke("Advertising as: $deviceId")
            }.addOnFailureListener { e ->
                isAdvertising = false
                Log.e(TAG, "Failed to start advertising", e)
                statusListener?.invoke("Failed to start advertising: ${e.message}")
            }
    }

    fun startDiscovery() {
        if (isDiscovering) {
            Log.w(TAG, "Already discovering")
            return
        }
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                isDiscovering = true
                Log.d(TAG, "Discovery started successfully")
                statusListener?.invoke("Searching for peers...")
            }.addOnFailureListener { e ->
                isDiscovering = false
                Log.e(TAG, "Failed to start discovery", e)
                statusListener?.invoke("Failed to start discovery: ${e.message}")
            }
    }

    fun stopAdvertising() {
        connectionsClient.stopAdvertising()
        isAdvertising = false
        Log.d(TAG, "Advertising stopped")
    }

    fun stopDiscovery() {
        connectionsClient.stopDiscovery()
        isDiscovering = false
        Log.d(TAG, "Discovery stopped")
    }

    fun stopP2P() {
        stopAdvertising()
        stopDiscovery()
        statusListener?.invoke("P2P stopped")
    }

    override fun sendMessage(message: Message) {
        if (connectedEndpoints.isEmpty()) {
            Log.w(TAG, "No connected endpoints to send message to")
            statusListener?.invoke("No peers connected to send message")
            return
        }
        try {
            // Increment hopCount for sending/forwarding
            val messageToSend = message.copy(hopCount = message.hopCount + 1)
            sendPayloadToAll(messageToSend)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            statusListener?.invoke("Failed to send message: ${e.message}")
        }
    }

    /**
     * Forward a received message to all peers except excluded endpoint
     */
    private fun forwardMessage(message: Message, excludeEndpoint: String? = null) {
        if (connectedEndpoints.isEmpty()) return
        val messageToForward = message.copy(hopCount = message.hopCount + 1)
        connectedEndpoints.filter { it != excludeEndpoint }.forEach { endpointId ->
            sendPayloadToEndpoint(endpointId, messageToForward)
        }
    }

    private fun sendPayloadToAll(message: Message) {
        connectedEndpoints.forEach { endpointId ->
            sendPayloadToEndpoint(endpointId, message)
        }
    }

    private fun sendPayloadToEndpoint(endpointId: String, message: Message) {
        val jsonString = serializeMessage(message)
        val bytes = jsonString.toByteArray(StandardCharsets.UTF_8)
        val payload = Payload.fromBytes(bytes)
        connectionsClient.sendPayload(endpointId, payload)
            .addOnSuccessListener {
                val readableTime = formatTimestamp(message.timestamp)
                Log.d(TAG, "Message sent at $readableTime to: $endpointId, hopCount: ${message.hopCount}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to send message to: $endpointId", e)
            }
        statusListener?.invoke("Message sent to ${connectedEndpoints.size} peer(s)")
    }

    fun shutdown() {
        connectedEndpoints.forEach { connectionsClient.disconnectFromEndpoint(it) }
        connectedEndpoints.clear()
        stopAdvertising()
        stopDiscovery()
        connectionsClient.stopAllEndpoints()
        Log.d(TAG, "P2PManager shutdown complete")
        statusListener?.invoke("P2P shutdown complete")
    }

    fun getConnectedPeerCount(): Int = connectedEndpoints.size
    fun getConnectedEndpoints(): Set<String> = connectedEndpoints.toSet()
    fun isAdvertising(): Boolean = isAdvertising
    fun isDiscovering(): Boolean = isDiscovering
    fun getDeviceId(): String = deviceId

    private fun serializeMessage(message: Message): String {
        val json = JSONObject().apply {
            put("id", message.id)
            put("senderId", message.senderId)
            put("content", message.content)
            put("timestamp", message.timestamp)
            put("latitude", message.latitude)
            put("longitude", message.longitude)
            put("ttl", message.ttl)
            put("hopCount", message.hopCount)
        }
        return json.toString()
    }

    private fun parseMessage(jsonString: String): Message {
        val json = JSONObject(jsonString)
        return Message(
            id = json.getString("id"),
            senderId = json.getString("senderId"),
            content = json.getString("content"),
            timestamp = json.getLong("timestamp"),
            latitude = json.optDouble("latitude", 0.0),
            longitude = json.optDouble("longitude", 0.0),
            ttl = json.optInt("ttl", 10),
            hopCount = json.optInt("hopCount", 0)
        )
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    data class PermissionStatus(val allGranted: Boolean, val missingPermissions: List<String>)
    data class ReadinessStatus(val ready: Boolean, val issues: List<String>)
}