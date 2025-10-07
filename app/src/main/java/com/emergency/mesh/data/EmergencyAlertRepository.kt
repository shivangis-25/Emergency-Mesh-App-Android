package com.emergency.mesh.data

import com.emergency.mesh.util.SMSManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmergencyAlertRepository(
    private val contactDao: EmergencyContactDao,
    private val smsManager: SMSManager
) {

    suspend fun sendEmergencyAlert(alertMessage: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val contacts = contactDao.getAllContacts().map {
                EmergencyContact(it.id, it.name, it.phoneNumber, it.relationship)
            }

            if (contacts.isEmpty()) {
                return@withContext Result.failure(
                    IllegalStateException("No emergency contacts configured")
                )
            }

            smsManager.sendEmergencyAlert(contacts, alertMessage)
        }
    }

    suspend fun addEmergencyContact(contact: EmergencyContact) {
        withContext(Dispatchers.IO) {
            val entity = EmergencyContactEntity(
                id = contact.id,
                name = contact.name,
                phoneNumber = contact.phoneNumber,
                relationship = contact.relationship,
                addedAt = System.currentTimeMillis()
            )
            contactDao.insertContact(entity)
        }
    }

    suspend fun getAllContacts(): List<EmergencyContact> {
        return withContext(Dispatchers.IO) {
            contactDao.getAllContacts().map {
                EmergencyContact(it.id, it.name, it.phoneNumber, it.relationship)
            }
        }
    }

    suspend fun deleteContact(contact: EmergencyContact) {
        withContext(Dispatchers.IO) {
            val entity = EmergencyContactEntity(
                id = contact.id,
                name = contact.name,
                phoneNumber = contact.phoneNumber,
                relationship = contact.relationship,
                addedAt = 0L
            )
            contactDao.deleteContact(entity)
        }
    }
}