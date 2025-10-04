package com.emergency.mesh.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.emergency.mesh.R
import com.emergency.mesh.data.Message
import com.emergency.mesh.data.MessageRepository
import com.emergency.mesh.data.RoomMessageRepository
import com.emergency.mesh.db.AppDatabase
import com.emergency.mesh.p2p.MeshManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapsActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private val messageViewModel: MessageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This line sets the user agent, a requirement to prevent getting banned from the OSM servers
        Configuration.getInstance().load(applicationContext, getSharedPreferences("OSM", MODE_PRIVATE))
        val messageViewModel: MessageViewModel by viewModels {
            val context = this@MapsActivity
            val database = AppDatabase.getDatabase(context) // Line 33
            val repository: MessageRepository = RoomMessageRepository(database.messageDao()) // Line 34
            val meshManager = MeshManager(context) // Line 35
            ViewModelFactory(repository, meshManager)
        }

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(9.5)
        // Default to a central point if no messages yet
        mapController.setCenter(GeoPoint(48.8583, 2.2944))

        messageViewModel.allMessages.observe(this) { messages ->
            updateMapMarkers(messages)
        }
    }

    private fun updateMapMarkers(messages: List<Message>) {
        map.overlays.clear()
        if (messages.isNotEmpty()) {
            messages.forEach { message ->
                val point = GeoPoint(message.latitude, message.longitude)
                val marker = Marker(map)
                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = message.text

                if (message.messageType == "SOS") {
                    marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_red_pin)
                } else {
                    marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_green_pin)
                }
                map.overlays.add(marker)
            }
            map.controller.setCenter(GeoPoint(messages.first().latitude, messages.first().longitude))
        }
        map.invalidate() // Redraw the map
    }

    public override fun onResume() {
        super.onResume()
        map.onResume()
    }

    public override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
