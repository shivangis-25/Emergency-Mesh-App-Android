package com.emergency.mesh.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emergency.mesh.R
import com.emergency.mesh.p2p.MeshManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity3 : AppCompatActivity() {

    private lateinit var meshManager: MeshManager
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var mapView: MapView
    private lateinit var tvPeerCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load osmdroid configuration before inflating layout
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_map)

        // ✅ Initialize mapView
        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        // Default location (Bengaluru for demo)
        val defaultPoint = GeoPoint(12.9716, 77.5946)
        mapView.controller.setCenter(defaultPoint)

        // Initialize Mesh + ViewModel
        meshManager = MeshManager(this)
        messageViewModel = MessageViewModel(meshManager)

        // TextView to show peer count
        tvPeerCount = findViewById(R.id.tv_peer_count)

        // Buttons
        val btnSOS = findViewById<Button>(R.id.btn_sos)
        val btnSafe = findViewById<Button>(R.id.btn_safe)

        // 🚨 SOS button
        btnSOS.setOnClickListener {
            val timestamp = getCurrentDateTime()
            val message = "🚨 SOS ALERT\n📍 Lat: ${defaultPoint.latitude}, Lon: ${defaultPoint.longitude}\n🕓 $timestamp"
            Toast.makeText(this, "Sending SOS...", Toast.LENGTH_SHORT).show()
            messageViewModel.sendSOS(defaultPoint.latitude, defaultPoint.longitude)
            addMarker(defaultPoint, message, R.drawable.ic_red_pin)
        }

        // ✅ I'm Safe button
        btnSafe.setOnClickListener {
            val timestamp = getCurrentDateTime()
            val message = "✅ I'M SAFE\n📍 Lat: ${defaultPoint.latitude}, Lon: ${defaultPoint.longitude}\n🕓 $timestamp"
            Toast.makeText(this, "Sending SAFE...", Toast.LENGTH_SHORT).show()
            messageViewModel.sendSafe(defaultPoint.latitude, defaultPoint.longitude)
            addMarker(defaultPoint, message, R.drawable.ic_green_pin)
        }

        // 👂 Listen for incoming messages
        messageViewModel.listenForMessages { message ->
            runOnUiThread {
                Toast.makeText(this, "Received: $message", Toast.LENGTH_LONG).show()

                // Extract latitude and longitude from message text
                val regex = Regex("Lat:\\s*([-\\d.]+),\\s*Lon:\\s*([-\\d.]+)")
                val matchResult = regex.find(message)

                if (matchResult != null) {
                    val latitude = matchResult.groupValues[1].toDouble()
                    val longitude = matchResult.groupValues[2].toDouble()
                    val point = GeoPoint(latitude, longitude)

                    // Choose icon based on message type
                    val icon = if (message.contains("SOS")) R.drawable.ic_red_pin else R.drawable.ic_green_pin

                    // Add marker to map
                    addMarker(point, message, icon)
                }
            }
        }

        // 🔁 Update peer count dynamically
        Thread {
            while (true) {
                runOnUiThread {
                    tvPeerCount.text = "Connected Peers: ${meshManager.getPeerCount()}"
                }
                Thread.sleep(3000)
            }
        }.start()
    }

    // 🧭 Add marker with message
    private fun addMarker(point: GeoPoint, title: String, iconRes: Int) {
        val marker = Marker(mapView)
        marker.position = point
        marker.title = title
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = getDrawable(iconRes)
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    // ⏰ Function to get formatted date-time
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy | HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
