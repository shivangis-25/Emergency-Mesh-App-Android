package com.emergency.mesh

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.emergency.mesh.data.EmergencyAlertRepository
import com.emergency.mesh.util.DatabaseProvider
import com.emergency.mesh.util.SMSManager
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationText: TextView
    private lateinit var emergencyRepository: EmergencyAlertRepository

    private val LOCATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize repository
        val database = DatabaseProvider.getDatabase(this)
        val smsManager = SMSManager(this)
        emergencyRepository = EmergencyAlertRepository(
            database.emergencyContactDao(),
            smsManager
        )

        // Initialize views
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationText = findViewById(R.id.locationText)
        val btnSendEmergency: Button = findViewById(R.id.btnSendEmergency)
        val btnManageContacts: Button = findViewById(R.id.btnManageContacts)

        // Send emergency alert
        btnSendEmergency.setOnClickListener {
            checkAndRequestPermissions()
        }

        // Open contact management screen
        btnManageContacts.setOnClickListener {
            val intent = Intent(this, ManageContactsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        val requiredPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS
        )

        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val locString = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                locationText.text = "Location: $locString"
                sendEmergencySmsToContacts(location.latitude, location.longitude)
            } else {
                requestNewLocation()
            }
        }.addOnFailureListener {
            requestNewLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000L
        ).setMaxUpdates(1).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val locString = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                        locationText.text = "Location: $locString"
                        sendEmergencySmsToContacts(location.latitude, location.longitude)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Location not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun sendEmergencySmsToContacts(latitude: Double, longitude: Double) {
        val message = """
🚨 Emergency! I need help.

📍 My current location:
Latitude: $latitude
Longitude: $longitude

🌐 Open in Google Maps:
https://maps.google.com/?q=$latitude,$longitude

(If you’re offline, open Google Maps and search these coordinates manually.)
""".trimIndent()

        val hasSmsPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

        Toast.makeText(this, "SMS Permission: $hasSmsPermission", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            val contacts = emergencyRepository.getAllContacts()
            Toast.makeText(this@MainActivity, "Contacts found: ${contacts.size}", Toast.LENGTH_SHORT).show()

            contacts.forEach {
                Toast.makeText(this@MainActivity, "Will send to: ${it.name} - ${it.phoneNumber}", Toast.LENGTH_LONG).show()
            }

            emergencyRepository.sendEmergencyAlert(message).fold(
                onSuccess = {
                    Toast.makeText(this@MainActivity, "✅ Emergency SMS sent successfully!", Toast.LENGTH_LONG).show()
                },
                onFailure = { error ->
                    Toast.makeText(this@MainActivity, "❌ ERROR: ${error.message}", Toast.LENGTH_LONG).show()
                    error.printStackTrace()
                }
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getLocation()
            } else {
                Toast.makeText(
                    this,
                    "Permissions denied. All permissions are required for emergency alerts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
