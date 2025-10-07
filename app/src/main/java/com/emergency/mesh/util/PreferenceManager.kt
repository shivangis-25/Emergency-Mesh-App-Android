package com.emergency.mesh.utils

import android.content.Context

object PreferencesManager {
    private const val PREFS_NAME = "mesh_prefs"
    private const val KEY_PHONE_NUMBER = "phone_number"

    fun savePhoneNumber(context: Context, phoneNumber: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PHONE_NUMBER, phoneNumber).apply()
    }

    fun getPhoneNumber(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }
}
