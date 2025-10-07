package com.emergency.mesh

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.emergency.mesh.data.EmergencyAlertRepository
import com.emergency.mesh.data.EmergencyContact
import com.emergency.mesh.util.DatabaseProvider
import com.emergency.mesh.util.SMSManager
import kotlinx.coroutines.launch
import java.util.UUID

class ManageContactsActivity : AppCompatActivity() {

    private lateinit var repository: EmergencyAlertRepository
    private lateinit var contactListView: LinearLayout
    private lateinit var etName: EditText
    private lateinit var etNumber: EditText
    private lateinit var etRelation: EditText
    private lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_contacts)

        val database = DatabaseProvider.getDatabase(this)
        repository = EmergencyAlertRepository(database.emergencyContactDao(), SMSManager(this))

        contactListView = findViewById(R.id.contactListView)
        etName = findViewById(R.id.etName)
        etNumber = findViewById(R.id.etNumber)
        etRelation = findViewById(R.id.etRelation)
        btnAdd = findViewById(R.id.btnAddContact)

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val number = etNumber.text.toString().trim()
            val relation = etRelation.text.toString().trim()

            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(this, "Enter name and phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = EmergencyContact(
                id = UUID.randomUUID().toString(),
                name = name,
                phoneNumber = number,
                relationship = if (relation.isEmpty()) null else relation
            )

            lifecycleScope.launch {
                repository.addEmergencyContact(contact)
                Toast.makeText(this@ManageContactsActivity, "Contact added", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etNumber.text.clear()
                etRelation.text.clear()
                loadContacts()
            }
        }

        loadContacts()
    }

    private fun loadContacts() {
        lifecycleScope.launch {
            val contacts = repository.getAllContacts()
            contactListView.removeAllViews()

            if (contacts.isEmpty()) {
                val emptyText = TextView(this@ManageContactsActivity).apply {
                    text = "No emergency contacts added."
                    textSize = 16f
                }
                contactListView.addView(emptyText)
                return@launch
            }

            contacts.forEach { contact ->
                val itemView = layoutInflater.inflate(R.layout.item_contact, contactListView, false)
                itemView.findViewById<TextView>(R.id.tvContactName).text = "${contact.name} (${contact.relationship ?: "N/A"})"
                itemView.findViewById<TextView>(R.id.tvContactNumber).text = contact.phoneNumber

                itemView.findViewById<ImageButton>(R.id.btnDeleteContact).setOnClickListener {
                    lifecycleScope.launch {
                        repository.deleteContact(contact)
                        Toast.makeText(this@ManageContactsActivity, "Contact deleted", Toast.LENGTH_SHORT).show()
                        loadContacts()
                    }
                }

                contactListView.addView(itemView)
            }
        }
    }
}
