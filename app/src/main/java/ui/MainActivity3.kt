package com.emergency.mesh.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emergency.mesh.R
import com.emergency.mesh.p2p.MeshManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity3 : AppCompatActivity() {

    private lateinit var meshManager: MeshManager
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_map)

        // 🔹 Initialize map properly
        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        val defaultPoint = GeoPoint(12.9716, 77.5946) // Bengaluru
        mapView.controller.setCenter(defaultPoint)

        // 🔹 Initialize Mesh
        meshManager = MeshManager(this)
        messageViewModel = MessageViewModel(meshManager)

        // 🔹 Buttons
        val btnSOS = findViewById<Button>(R.id.btn_sos)
        val btnSafe = findViewById<Button>(R.id.btn_safe)
        val btnViewMap = findViewById<Button>(R.id.btn_view_map)

        // 🚨 SOS button
        btnSOS.setOnClickListener {
            Toast.makeText(this, "Sending SOS...", Toast.LENGTH_SHORT).show()
            messageViewModel.sendSOS(defaultPoint.latitude, defaultPoint.longitude)
            addMarker(defaultPoint, "🚨 SOS ALERT", R.drawable.ic_red_pin)
        }

        // ✅ I'm Safe button
        btnSafe.setOnClickListener {
            Toast.makeText(this, "Sending SAFE...", Toast.LENGTH_SHORT).show()
            messageViewModel.sendSafe(defaultPoint.latitude, defaultPoint.longitude)
            addMarker(defaultPoint, "✅ I'm Safe", R.drawable.ic_green_pin)
        }

        // 🗺️ View Map button — recenter
        btnViewMap.setOnClickListener {
            mapView.controller.animateTo(defaultPoint)
        }

        // 👂 Receive mesh messages
        messageViewModel.listenForMessages { message ->
            runOnUiThread {
                Toast.makeText(this, "Received: $message", Toast.LENGTH_LONG).show()

                // Parse if it's SOS or Safe (simple string check)
                if (message.contains("SOS")) {
                    addMarker(defaultPoint, message, R.drawable.ic_red_pin)
                } else if (message.contains("SAFE")) {
                    addMarker(defaultPoint, message, R.drawable.ic_green_pin)
                }
            }
        }
    }

    // 🔹 Function to add markers
    private fun addMarker(point: GeoPoint, title: String, iconRes: Int) {
        val marker = Marker(mapView)
        marker.position = point
        marker.title = title
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = getDrawable(iconRes)
        mapView.overlays.add(marker)
        mapView.invalidate()
    }
}
