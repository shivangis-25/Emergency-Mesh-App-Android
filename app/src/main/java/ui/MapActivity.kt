package com.emergency.mesh.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.emergency.mesh.R
import com.emergency.mesh.p2p.MeshManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var meshManager: MeshManager
    private lateinit var messageViewModel: MessageViewModel

    private var userMarker: Marker? = null
    private var lastKnownLocation: Location? = null

    private val LOCATION_PERMISSION_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load OSMdroid configuration
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setContentView(R.layout.activity_map)

        // Initialize map and location
        mapView = findViewById(R.id.map)
        mapView.setMultiTouchControls(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Mesh + ViewModel
        meshManager = MeshManager(this)
        val factory = ViewModelFactory(meshManager)
        messageViewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        val btnAlert = findViewById<Button>(R.id.btn_sos)
        val btnSafe = findViewById<Button>(R.id.btn_safe)

        btnAlert.setOnClickListener { getLocationAndSend(isSOS = true) }
        btnSafe.setOnClickListener { getLocationAndSend(isSOS = false) }

        // Listen for incoming P2P messages
        messageViewModel.listenForMessages { message ->
            runOnUiThread { handleIncomingMessage(message) }
        }

        requestLocationPermission()
    }

    private fun getLocationAndSend(isSOS: Boolean) {
        if (!checkLocationPermission()) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastKnownLocation = location
                updateMarker(location.latitude, location.longitude, isSOS, "Me")

                if (isSOS) messageViewModel.sendSOS(location.latitude, location.longitude)
                else messageViewModel.sendSafe(location.latitude, location.longitude)

                Toast.makeText(
                    this,
                    "Broadcasted ${if (isSOS) "SOS" else "Safe"}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMarker(lat: Double, lon: Double, isSOS: Boolean, titlePrefix: String) {
        val point = GeoPoint(lat, lon)

        userMarker?.let { mapView.overlays.remove(it) }

        val marker = Marker(mapView)
        val currentTime = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()).format(Date())

        marker.position = point
        marker.title = "$titlePrefix: ${if (isSOS) "🚨 SOS ALERT" else "✅ I'm Safe"}"
        marker.subDescription = "Lat: %.5f, Lon: %.5f\nTime: %s".format(lat, lon, currentTime)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        val drawableId = if (isSOS) R.drawable.ic_red_pin else R.drawable.ic_green_pin
        marker.icon = ContextCompat.getDrawable(this, drawableId)

        mapView.overlays.add(marker)
        userMarker = marker

        mapView.controller.setCenter(point)
        mapView.controller.setZoom(17.5)
        mapView.invalidate()
    }

    private fun handleIncomingMessage(message: String) {
        try {
            val parts = message.split("|")
            if (parts.size >= 3) {
                val type = parts[0]
                val lat = parts[1].toDoubleOrNull()
                val lon = parts[2].toDoubleOrNull()
                if (lat != null && lon != null) {
                    val isSOS = type.equals("SOS", ignoreCase = true)
                    updateMarker(lat, lon, isSOS, "Peer")
                    Toast.makeText(this, "Received: $type @ $lat, $lon", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            false
        } else true
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc -> lastKnownLocation = loc }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    lastKnownLocation = loc
                }
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
