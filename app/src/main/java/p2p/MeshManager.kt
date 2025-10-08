package com.emergency.mesh.p2p

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.AsyncTask
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class MeshManager(private val context: Context) {

    private val manager: WifiP2pManager
    private val channel: WifiP2pManager.Channel
    private var peers: MutableList<WifiP2pDevice> = mutableListOf()
    private var connectionInfo: WifiP2pInfo? = null
    private var serverTask: ServerTask? = null

    var onMessageReceived: ((String) -> Unit)? = null

    init {
        manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(context, context.mainLooper, null)
        registerReceiver()
        discoverPeers()
    }

    // 🔹 Discover available peers (with permission check)
    @SuppressLint("MissingPermission")
    private fun discoverPeers() {
        if (!hasLocationPermission()) {
            Log.e("MeshManager", "❌ Missing permissions, cannot discover peers")
            return
        }

        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("MeshManager", "✅ Peer discovery started")
            }

            override fun onFailure(reason: Int) {
                Log.e("MeshManager", "❌ Peer discovery failed: $reason")
            }
        })
    }

    // 🔹 Check if required permissions are granted
    private fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val nearbyDevices = ContextCompat.checkSelfPermission(
            context, Manifest.permission.NEARBY_WIFI_DEVICES
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || nearbyDevices
    }

    // 🔹 Register BroadcastReceiver for Wi-Fi Direct events
    @SuppressLint("MissingPermission")
    private fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {

                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        if (hasLocationPermission()) {
                            manager.requestPeers(channel) { peerList ->
                                peers.clear()
                                peers.addAll(peerList.deviceList)
                                Log.d("MeshManager", "👥 Found ${peers.size} peers")

                                // Auto-connect to first available peer if not connected
                                if (connectionInfo == null && peers.isNotEmpty()) {
                                    connectToPeer(peers[0])
                                }
                            }
                        }
                    }

                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        val networkInfo =
                            intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                        if (networkInfo != null && networkInfo.isConnected) {
                            manager.requestConnectionInfo(channel) { info ->
                                connectionInfo = info
                                if (info.groupFormed && info.isGroupOwner) {
                                    Log.d("MeshManager", "🟢 This device is Group Owner (GO)")
                                    serverTask = ServerTask(onMessageReceived)
                                    serverTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                                } else {
                                    Log.d("MeshManager", "🟠 Connected as Client")
                                }
                            }
                        }
                    }
                }
            }
        }, intentFilter)
    }

    // 🔹 Auto-connect to peer to speed up network formation
    @SuppressLint("MissingPermission")
    private fun connectToPeer(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("MeshManager", "🔗 Connection initiated with ${device.deviceName}")
            }

            override fun onFailure(reason: Int) {
                Log.e("MeshManager", "⚠️ Connection failed: $reason")
            }
        })
    }

    // 🔹 Send message
    fun sendMessage(message: String) {
        Log.d("MeshManager", "📤 Sending message: $message")
        broadcastMessage(message)
    }

    // 🔹 Broadcast message to peers
    @SuppressLint("StaticFieldLeak")
    private fun broadcastMessage(message: String) {
        if (connectionInfo?.isGroupOwner == true) {
            // Group Owner sends to all connected clients
            for (peer in peers) {
                SendTask(peer.deviceAddress, message)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        } else {
            // Client sends only to GO
            connectionInfo?.groupOwnerAddress?.hostAddress?.let { ownerAddress ->
                SendTask(ownerAddress, message)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        }
    }

    // 🔹 Async task to send message to one peer
    private class SendTask(
        private val host: String,
        private val message: String
    ) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val socket = Socket(host, 8988)
                val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                writer.write(message)
                writer.newLine()
                writer.flush()
                writer.close()
                socket.close()
                Log.d("MeshManager", "📨 Message sent to $host")
            } catch (e: Exception) {
                Log.e("MeshManager", "SendTask error: ${e.message}")
            }
            return null
        }
    }

    // 🔹 Server to receive messages
    private class ServerTask(
        private val messageListener: ((String) -> Unit)?
    ) : AsyncTask<Void, String, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val serverSocket = ServerSocket(8988)
                Log.d("MeshManager", "🖥️ Server started, waiting for messages...")

                while (true) {
                    val client = serverSocket.accept()
                    val reader = BufferedReader(InputStreamReader(client.getInputStream()))
                    val message = reader.readLine()
                    if (!message.isNullOrEmpty()) {
                        publishProgress(message)
                    }
                    reader.close()
                    client.close()
                }
            } catch (e: Exception) {
                Log.e("MeshManager", "Server error: ${e.message}")
            }
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {
            values[0]?.let { msg ->
                messageListener?.invoke(msg)
            }
        }
    }

    // 🔹 Utility method to expose peer count for UI
    fun getPeerCount(): Int {
        return peers.size
    }
}
