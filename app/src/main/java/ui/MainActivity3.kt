package com.emergency.mesh.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.emergency.mesh.R
import com.emergency.mesh.p2p.MeshManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class MainActivity3 : AppCompatActivity() {

    private lateinit var meshManager: MeshManager
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var mapView: MapView
    private lateinit var tvPeerCount: TextView

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.NEARBY_WIFI_DEVICES
    )
    private val REQUEST_CODE_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load OSM configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(R.layout.activity_map)

        // Initialize map
        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        // Default center (Bengaluru)
        val defaultPoint = GeoPoint(12.9716, 77.5946)
        mapView.controller.setCenter(defaultPoint)

        // TextView to show peer count
        tvPeerCount = findViewById(R.id.tv_peer_count)
        tvPeerCount.text = "Connected Peers: 0"

        // ✅ Request permissions
        checkAndRequestPermissions()

        // ✅ Initialize MeshManager and ViewModel
        meshManager = MeshManager(this)
        messageViewModel = MessageViewModel(meshManager)

        val btnSOS = findViewById<Button>(R.id.btn_sos)
        val btnSafe = findViewById<Button>(R.id.btn_safe)

        // 🚨 Send SOS message
        btnSOS.setOnClickListener {
            val timestamp = getCurrentDateTime()
            val message =
                "🚨 SOS ALERT\n📍 Lat: ${defaultPoint.latitude}, Lon: ${defaultPoint.longitude}\n🕓 $timestamp"
            Toast.makeText(this, "Sending SOS...", Toast.LENGTH_SHORT).show()
            messageViewModel.sendSOS(defaultPoint.latitude, defaultPoint.longitude)
            addMarker(defaultPoint, message, R.drawable.ic_red_pin)
        }

        // ✅ Send I'm Safe message
        btnSafe.setOnClickListener {
            val timestamp = getCurrentDateTime()
            val message =
                "✅ I'M SAFE\n📍 Lat: ${defaultPoint.latitude}, Lon: ${defaultPoint.longitude}\n🕓 $timestamp"
            Toast.makeText(this, "Sending SAFE...", Toast.LENGTH_SHORT).show()
            messageViewModel.sendSafe(defaultPoint.latitude, defaultPoint.longitude)
            addMarker(defaultPoint, message, R.drawable.ic_green_pin)
        }

        // 👂 Listen for incoming messages
        messageViewModel.listenForMessages { message ->
            runOnUiThread {
                val icon =
                    if (message.contains("SOS")) R.drawable.ic_red_pin else R.drawable.ic_green_pin
                addMarker(defaultPoint, message, icon)
                Toast.makeText(this, "Received: $message", Toast.LENGTH_LONG).show()
            }
        }

        // 🔄 Update peer count dynamically
        meshManager.onPeerCountChanged = { count ->
            runOnUiThread {
                tvPeerCount.text = "Connected Peers: $count"
            }
        }
    }

    // 🧭 Add map marker
    private fun addMarker(point: GeoPoint, title: String, iconRes: Int) {
        val marker = Marker(mapView)
        marker.position = point
        marker.title = title
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.icon = getDrawable(iconRes)
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    // ⏰ Get formatted date-time
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy | HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // ✅ Permissions handling
    private fun hasAllPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { perm ->
            ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkAndRequestPermissions() {
        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "✅ Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ Permissions denied!", Toast.LENGTH_LONG).show()
            }
        }
    }
}

