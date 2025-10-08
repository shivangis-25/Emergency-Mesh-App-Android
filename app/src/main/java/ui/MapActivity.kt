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

        // === IMPORTANT: load osmdroid config then set content view ===
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        // <-- this line MUST be here so findViewById can find views in activity_map.xml
        setContentView(R.layout.activity_map)

        // Map & location
        mapView = findViewById(R.id.map)
        mapView.setMultiTouchControls(true)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // P2P Mesh and ViewModel
        meshManager = MeshManager(this)
        val factory = ViewModelFactory(meshManager)
        messageViewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        // Buttons (IDs must match activity_map.xml)
        val btnAlert = findViewById<Button>(R.id.btn_sos)     // SOS
        val btnSafe = findViewById<Button>(R.id.btn_safe)       // I'm Safe
        val btnViewMap = findViewById<Button>(R.id.btn_view_map)

        btnViewMap.setOnClickListener { centerOnLastLocation() }

        btnAlert.setOnClickListener {
            getLocationAndSend(isSOS = true)
        }

        btnSafe.setOnClickListener {
            getLocationAndSend(isSOS = false)
        }

        // Listen for incoming P2P messages from other peers
        messageViewModel.listenForMessages { message ->
            runOnUiThread {
                handleIncomingMessage(message)
            }
        }

        // Request location permission and attempt to get last location
        requestLocationPermission()
    }

    private fun centerOnLastLocation() {
        if (lastKnownLocation != null) {
            val p = GeoPoint(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            mapView.controller.setCenter(p)
            mapView.controller.setZoom(18.5)
            mapView.invalidate()
        } else {
            Toast.makeText(this, "Location not available yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocationAndSend(isSOS: Boolean) {
        if (!checkLocationPermission()) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastKnownLocation = location
                // update local marker
                updateMarker(location.latitude, location.longitude, isSOS, "Me")
                // broadcast via ViewModel
                if (isSOS) messageViewModel.sendSOS(location.latitude, location.longitude)
                else messageViewModel.sendSafe(location.latitude, location.longitude)
                Toast.makeText(this, "Broadcasted ${if (isSOS) "SOS" else "Safe"}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMarker(lat: Double, lon: Double, isSOS: Boolean, titlePrefix: String) {
        val point = GeoPoint(lat, lon)

        // remove previous marker for this sender (we keep single local marker as example)
        userMarker?.let { mapView.overlays.remove(it) }

        val marker = Marker(mapView)
        marker.position = point
        marker.title = "$titlePrefix: ${if (isSOS) "🚨 SOS" else "✅ Safe"}"
        marker.subDescription = "Lat: $lat, Lng: $lon"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // use drawable resources red_marker / green_marker
        val drawableId = if (isSOS) R.drawable.ic_red_pin else R.drawable.ic_green_pin
        marker.icon = ContextCompat.getDrawable(this, drawableId)

        mapView.overlays.add(marker)
        userMarker = marker
        mapView.controller.setCenter(point)
        mapView.invalidate()
    }

    private fun handleIncomingMessage(message: String) {
        // expected format: "SOS|lat|lon" or "SAFE|lat|lon" (see MessageViewModel implementation)
        try {
            val parts = message.split("|")
            if (parts.size >= 3) {
                val type = parts[0]
                val lat = parts[1].toDoubleOrNull()
                val lon = parts[2].toDoubleOrNull()
                if (lat != null && lon != null) {
                    // for remote peers we create a new marker (or update map as you prefer)
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
            // prime lastKnownLocation
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                lastKnownLocation = loc
            }
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
                    centerOnLastLocation()
                }
            } else {
                Toast.makeText(this, "Location permission required to use the map", Toast.LENGTH_LONG).show()
            }
        }
    }
}
