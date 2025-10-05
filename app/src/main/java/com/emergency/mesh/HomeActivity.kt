package com.emergency.mesh

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize buttons
        val btnEmergencySms: Button = findViewById(R.id.btnEmergencySms)
        val btnMeshNetwork: Button = findViewById(R.id.btnMeshNetwork)
        val btnEmergencyMap: Button = findViewById(R.id.btnEmergencyMap)

        // Emergency SMS Feature (Dev2)
        btnEmergencySms.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Mesh Network Feature (Dev1)
        btnMeshNetwork.setOnClickListener {
            val intent = Intent(this, MainActivity1::class.java)
            startActivity(intent)
        }

        // Emergency Map Feature (Dev3)
        btnEmergencyMap.setOnClickListener {
            val intent = Intent(this, com.emergency.mesh.ui.MainActivity3::class.java)
            startActivity(intent)
        }
    }
}