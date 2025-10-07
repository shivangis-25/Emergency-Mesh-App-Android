package com.emergency.mesh

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.emergnecymesh.model.Message
import com.example.emergnecymesh.p2p.P2PManager
import java.util.UUID

class MainActivity1 : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var p2pManager: P2PManager
    private lateinit var deviceId: String

    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var statusText: TextView
    private lateinit var receivedText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        // Generate unique device ID
        deviceId = "Device_${UUID.randomUUID().toString().substring(0, 8)}"

        initializeViews()
        checkAndRequestPermissions()
    }

    private fun initializeViews() {
        messageInput = findViewById(R.id.etMessage)
        sendButton = findViewById(R.id.btnSend)
        startButton = findViewById(R.id.btnStartP2P)
        stopButton = findViewById(R.id.btnStopP2P)
        statusText = findViewById(R.id.tvStatus)
        receivedText = findViewById(R.id.tvMessages)

        startButton.setOnClickListener { startP2P() }
        stopButton.setOnClickListener { stopP2P() }
        sendButton.setOnClickListener { sendMessage() }

        updateStatus("Ready to start")
    }


    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        // Bluetooth permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        // Location permissions (required for Nearby)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // Wi-Fi permissions
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE)
        permissions.add(Manifest.permission.CHANGE_WIFI_STATE)

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            initializeP2P()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeP2P()
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Permissions required for P2P communication",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initializeP2P() {
        p2pManager = P2PManager(this, deviceId)

        // Set up message listener
        p2pManager.listener = { message ->
            runOnUiThread {
                val formattedTime = P2PManager.formatTimestamp(message.timestamp)
                val receivedMsg = "From: ${message.senderId}\n" +
                        "Content: ${message.content}\n" +
                        "Time: $formattedTime\n" +
                        "Hops: ${message.hopCount}\n\n"

                receivedText.text = receivedMsg + receivedText.text
                // ... rest of code
            }
        }
        updateStatus("P2P Manager initialized - Device: $deviceId")
    }

    private fun startP2P() {
        try {
            p2pManager.startAdvertising()
            p2pManager.startDiscovery()

            updateStatus("Advertising and discovering... Connected peers: 0")
            Toast.makeText(this, "P2P started", Toast.LENGTH_SHORT).show()

            // Update connected peer count periodically
            startButton.postDelayed(object : Runnable {
                override fun run() {
                    val peerCount = p2pManager.getConnectedPeerCount()
                    updateStatus("Active - Connected peers: $peerCount")
                    startButton.postDelayed(this, 2000)
                }
            }, 2000)

        } catch (e: Exception) {
            Log.e(TAG, "Error starting P2P", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopP2P() {
        try {
            p2pManager.shutdown()
            updateStatus("P2P stopped")
            Toast.makeText(this, "P2P stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping P2P", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendMessage() {
        val content = messageInput.text.toString().trim()

        if (content.isEmpty()) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        if (p2pManager.getConnectedPeerCount() == 0) {
            Toast.makeText(this, "No peers connected", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val message = Message(
                senderId = deviceId,
                content = content,
                latitude = 0.0, // Add GPS integration later
                longitude = 0.0
            )

            p2pManager.sendMessage(message)

            messageInput.text.clear()
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Message sent: $message")

        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            Toast.makeText(this, "Error sending: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateStatus(status: String) {
        statusText.text = status
        Log.d(TAG, status)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::p2pManager.isInitialized) {
            p2pManager.shutdown()
        }
    }
}