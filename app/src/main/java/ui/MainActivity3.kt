package com.emergency.mesh.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.emergency.mesh.data.MessageRepository2
import com.emergency.mesh.data.RoomMessageRepository
import com.emergency.mesh.databinding.ActivityMainBinding
import com.emergency.mesh.db.AppDatabase
import com.emergency.mesh.p2p.MeshManager
import com.google.android.gms.location.LocationServices

// The import paths from your original code have been corrected here.
// For example, ViewModelFactory is now imported from com.emergency.mesh.ui, not com.emergency.mesh.ui.viewmodel

class `MainActivity3` : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val messageAdapter = MessageAdapter()
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private val messageViewModel: MessageViewModel by viewModels {
        val context = this@`MainActivity3`
        val database = AppDatabase.getDatabase(context) // Line 33
        val repository: MessageRepository2 = RoomMessageRepository(database.messageDao()) // Line 34
        val meshManager = MeshManager(context) // Line 35
        ViewModelFactory(repository, meshManager)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                // You might want to automatically send a message here if one was pending
            } else {
                Toast.makeText(this, "Location permission is required to send messages", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        observeMessages()
        checkLocationPermission()
    }

    private fun setupRecyclerView() {
        binding.messagesRecyclerView.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(this@`MainActivity3`)
        }
    }

    private fun observeMessages() {
        messageViewModel.allMessages.observe(this) { messages ->
            messageAdapter.submitList(messages.reversed())
        }
    }

    private fun setupClickListeners() {
        binding.sosButton.setOnClickListener {
            sendMessage("SOS", "SOS")
        }
        binding.safeButton.setOnClickListener {
            sendMessage("I'm Safe", "SAFE")
        }
        binding.viewMapButton.setOnClickListener {
            val messages = messageViewModel.allMessages.value ?: emptyList()
            if (messages.isNotEmpty()) {
                val intent = Intent(this, MapsActivity::class.java).apply {
                    putParcelableArrayListExtra("MESSAGES_LIST", ArrayList(messages))
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No messages to display on map.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessage(text: String, type: String) {
        if (hasLocationPermission()) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        messageViewModel.sendMessage(text, type, location.latitude, location.longitude)
                        Toast.makeText(this, "$type message saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Could not get current location. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                // This catch is good practice, though hasLocationPermission() should prevent it.
                Toast.makeText(this, "Location access denied.", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

