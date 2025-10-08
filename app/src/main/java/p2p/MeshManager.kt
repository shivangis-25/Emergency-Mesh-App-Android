package com.emergency.mesh.p2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.*
import android.os.AsyncTask
import android.util.Log
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class MeshManager(private val context: Context) {

    private val manager: WifiP2pManager
    private val channel: WifiP2pManager.Channel
    private var peers: MutableList<WifiP2pDevice> = mutableListOf()
    private var connectionInfo: WifiP2pInfo? = null
    private var serverTask: ServerTask? = null

    // 👇 Callback for delivering incoming messages
    var onMessageReceived: ((String) -> Unit)? = null

    init {
        manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(context, context.mainLooper, null)
        registerReceiver()
        discoverPeers()
    }

    // 🔹 Discover available peers
    private fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("MeshManager", "Peer discovery started.")
            }

            override fun onFailure(reason: Int) {
                Log.e("MeshManager", "Peer discovery failed: $reason")
            }
        })
    }

    // 🔹 Register BroadcastReceiver for Wi-Fi Direct events
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
                        manager.requestPeers(channel) { peerList ->
                            peers.clear()
                            peers.addAll(peerList.deviceList)
                            Log.d("MeshManager", "Found ${peers.size} peers.")
                        }
                    }

                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        val networkInfo =
                            intent.getParcelableExtra<android.net.NetworkInfo>(
                                WifiP2pManager.EXTRA_NETWORK_INFO
                            )
                        if (networkInfo != null && networkInfo.isConnected) {
                            manager.requestConnectionInfo(channel) { info ->
                                connectionInfo = info
                                if (info.groupFormed && info.isGroupOwner) {
                                    // Start server to listen for incoming messages
                                    serverTask = ServerTask(onMessageReceived)
                                    serverTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                                }
                            }
                        }
                    }
                }
            }
        }, intentFilter)
    }

    // 🔹 Core: Send message to all connected peers
    fun sendMessage(message: String) {
        Log.d("MeshManager", "Sending message: $message")
        broadcastMessage(message)
    }

    // 🔹 Broadcast message (called internally by sendMessage)
    public fun broadcastMessage(message: String) {
        Log.d("MeshManager", "Broadcasting message: $message")

        // If device is group owner → send to clients
        if (connectionInfo?.isGroupOwner == true) {
            for (peer in peers) {
                SendTask(peer.deviceAddress, message)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        } else {
            // If not group owner → send to group owner
            connectionInfo?.groupOwnerAddress?.hostAddress?.let { ownerAddress ->
                SendTask(ownerAddress, message)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        }
    }

    // 🔹 Task for sending a message to one peer
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
                Log.d("MeshManager", "Message sent to $host: $message")
            } catch (e: Exception) {
                Log.e("MeshManager", "SendTask error: ${e.message}")
            }
            return null
        }
    }

    // 🔹 Server task for listening to incoming messages
    private class ServerTask(
        private val messageListener: ((String) -> Unit)?
    ) : AsyncTask<Void, String, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val serverSocket = ServerSocket(8988)
                Log.d("MeshManager", "Server started, waiting for messages...")

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
}
