package com.chichuka.birdvgvardc.ror.data.shar

import android.content.Context
import androidx.core.content.edit

class TowerPlannerSharedPreference(context: Context) {
    private val chickenPrefs = context.getSharedPreferences("farmcraftSharedPrefsAb", Context.MODE_PRIVATE)

    var chickenSavedUrl: String
        get() = chickenPrefs.getString(TP_SAVED_URL, "") ?: ""
        set(value) = chickenPrefs.edit { putString(TP_SAVED_URL, value) }

    var chickenExpired : Long
        get() = chickenPrefs.getLong(TP_EXPIRED, 0L)
        set(value) = chickenPrefs.edit { putLong(TP_EXPIRED, value) }

    var chickenAppState: Int
        get() = chickenPrefs.getInt(RP_APPLICATION_STATE, 0)
        set(value) = chickenPrefs.edit { putInt(RP_APPLICATION_STATE, value) }

    var chickenNotificationRequest: Long
        get() = chickenPrefs.getLong(TP_NOTIFICAITON_REQUEST, 0L)
        set(value) = chickenPrefs.edit { putLong(TP_NOTIFICAITON_REQUEST, value) }

    var chickenNotificationRequestedBefore: Boolean
        get() = chickenPrefs.getBoolean(TP_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = chickenPrefs.edit { putBoolean(
            TP_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val TP_SAVED_URL = "tpSavedUrl"
        private const val TP_EXPIRED = "tpExpired"
        private const val RP_APPLICATION_STATE = "tpApplicationState"
        private const val TP_NOTIFICAITON_REQUEST = "tpNotificationRequest"
        private const val TP_NOTIFICATION_REQUEST_BEFORE = "tpNotificationRequestedBefore"
    }
}