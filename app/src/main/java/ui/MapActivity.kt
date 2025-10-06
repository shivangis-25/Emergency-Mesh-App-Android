package com.emergency.mesh.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.emergency.mesh.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var btnViewMap: Button
    private lateinit var btnSOS: Button
    private lateinit var btnSafe: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationMarker: Marker? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        map.setMultiTouchControls(true)
        map.controller.setZoom(17.0)

        checkLocationPermission()

        // Buttons
        btnViewMap.setOnClickListener {
            Toast.makeText(this, "Map is already displayed!", Toast.LENGTH_SHORT).show()
        }

        btnSOS.setOnClickListener {
            Toast.makeText(this, "🚨 SOS Alert Sent!", Toast.LENGTH_SHORT).show()
        }

        btnSafe.setOnClickListener {
            Toast.makeText(this, "✅ Marked Safe!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showLocationOnMap(location)
                } else {
                    Toast.makeText(this, "Unable to fetch location!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showLocationOnMap(location: Location) {
        val userLocation = GeoPoint(location.latitude, location.longitude)
        map.controller.setCenter(userLocation)

        if (locationMarker == null) {
            locationMarker = Marker(map)
            locationMarker!!.title = "You are here"
            map.overlays.add(locationMarker)
        }

        locationMarker!!.position = userLocation
        map.invalidate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show()
            }
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
