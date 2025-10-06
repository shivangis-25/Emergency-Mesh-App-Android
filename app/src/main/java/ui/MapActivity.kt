package com.emergency.mesh.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.emergency.mesh.R
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var btnViewMap: Button
    private lateinit var btnSOS: Button
    private lateinit var btnSafe: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Required for osmdroid
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid_prefs", MODE_PRIVATE)
        )


        map = findViewById(R.id.map)
        btnViewMap = findViewById(R.id.btn_view_map)
        btnSOS = findViewById(R.id.btn_sos)
        btnSafe = findViewById(R.id.btn_safe)

        // Map setup
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(28.6139, 77.2090) // Example: Delhi
        mapController.setCenter(startPoint)

        // Add a marker at startPoint
        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.title = "You are here"
        map.overlays.add(startMarker)

        // Button listeners
        btnViewMap.setOnClickListener {
            Toast.makeText(this, "Map is already displayed!", Toast.LENGTH_SHORT).show()
        }

        btnSOS.setOnClickListener {
            Toast.makeText(this, "SOS Alert Sent!", Toast.LENGTH_SHORT).show()
            // TODO: Connect with P2PManager.sendMessage()
        }

        btnSafe.setOnClickListener {
            Toast.makeText(this, "Marked Safe!", Toast.LENGTH_SHORT).show()
            // TODO: Connect with P2PManager.sendMessage()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}

