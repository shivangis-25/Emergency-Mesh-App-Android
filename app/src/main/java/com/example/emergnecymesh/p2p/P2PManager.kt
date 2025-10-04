package com.example.emergnecymesh.p2p


import android.content.Context
import android.util.Log
import com.example.emergnecymesh.model.Message
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/**
 * P2PManager handles peer-to-peer communication using Google Nearby Connections API.
 * Implements advertising, discovery, and message sending/receiving.
 */
class P2PManager(
    private val context: Context,
    private val deviceId: String
) : P2PSender {

    companion object {
        private const val TAG = "P2PManager"
        private const val SERVICE_ID = "com.emergency.mesh.p2p"
        private val STRATEGY = Strategy.P2P_CLUSTER

    }

    // Listener for received messages
    var listener: ((Message) -> Unit)? = null

    // Nearby Connections client
    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)

    // Track connected endpoints
    private val connectedEndpoints = mutableSetOf<String>()

    // Connection lifecycle callback
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "Connection initiated with: ${info.endpointName} (ID: $endpointId)")

            // Auto-accept all connections in emergency mesh
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "Connected successfully to: $endpointId")
                    connectedEndpoints.add(endpointId)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.w(TAG, "Connection rejected by: $endpointId")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.e(TAG, "Connection error with: $endpointId")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Disconnected from: $endpointId")
            connectedEndpoints.remove(endpointId)
        }
    }

    // Payload callback for receiving messages
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val bytes = payload.asBytes()
                if (bytes != null) {
                    try {
                        val jsonString = String(bytes, StandardCharsets.UTF_8)
                        val message = parseMessage(jsonString)
                        Log.d(TAG, "Message received from $endpointId: ${message.content}")
                        listener?.invoke(message)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing received message", e)
                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                Log.d(TAG, "Payload sent successfully to: $endpointId")
            } else if (update.status == PayloadTransferUpdate.Status.FAILURE) {
                Log.e(TAG, "Payload failed to send to: $endpointId")
            }
        }
    }

    // Endpoint discovery callback
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "Endpoint found: ${info.endpointName} (ID: $endpointId)")

            // Auto-connect to discovered endpoints
            connectionsClient.requestConnection(
                deviceId,
                endpointId,
                connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "Connection request sent to: $endpointId")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to request connection to: $endpointId", e)
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "Endpoint lost: $endpointId")
        }
    }

    /**
     * Start advertising this device so others can discover it
     */
    fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(STRATEGY)
            .build()

        connectionsClient.startAdvertising(
            deviceId,
            SERVICE_ID,
            connectionLifecycleCallback,
            advertisingOptions
        ).addOnSuccessListener {
            Log.d(TAG, "Advertising started successfully")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to start advertising", e)
        }
    }

    /**
     * Start discovering nearby devices
     */
    fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(STRATEGY)
            .build()

        connectionsClient.startDiscovery(
            SERVICE_ID,
            endpointDiscoveryCallback,
            discoveryOptions
        ).addOnSuccessListener {
            Log.d(TAG, "Discovery started successfully")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to start discovery", e)
        }
    }

    /**
     * Stop advertising
     */
    fun stopAdvertising() {
        connectionsClient.stopAdvertising()
        Log.d(TAG, "Advertising stopped")
    }

    /**
     * Stop discovering
     */
    fun stopDiscovery() {
        connectionsClient.stopDiscovery()
        Log.d(TAG, "Discovery stopped")
    }

    /**
     * Send a message to all connected endpoints
     */
    override fun sendMessage(message: Message) {
        if (connectedEndpoints.isEmpty()) {
            Log.w(TAG, "No connected endpoints to send message to")
            return
        }

        try {
            val jsonString = serializeMessage(message)
            val bytes = jsonString.toByteArray(StandardCharsets.UTF_8)
            val payload = Payload.fromBytes(bytes)

            connectedEndpoints.forEach { endpointId ->
                connectionsClient.sendPayload(endpointId, payload)
                    .addOnSuccessListener {
                        Log.d(TAG, "Message queued for sending to: $endpointId")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to send message to: $endpointId", e)
                    }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
        }
    }

    /**
     * Disconnect from all endpoints and stop all operations
     */
    fun shutdown() {
        connectedEndpoints.forEach { endpointId ->
            connectionsClient.disconnectFromEndpoint(endpointId)
        }
        connectedEndpoints.clear()

        stopAdvertising()
        stopDiscovery()

        connectionsClient.stopAllEndpoints()
        Log.d(TAG, "P2PManager shutdown complete")
    }

    /**
     * Get the number of currently connected peers
     */
    fun getConnectedPeerCount(): Int = connectedEndpoints.size

    /**
     * Get list of connected endpoint IDs
     */
    fun getConnectedEndpoints(): Set<String> = connectedEndpoints.toSet()

    /**
     * Serialize Message to JSON string
     */
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

    /**
     * Parse JSON string to Message
     */
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
}

/**
 * Interface for sending P2P messages
 * Can be injected into other modules (e.g., MeshManager)
 */