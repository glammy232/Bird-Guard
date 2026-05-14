package com.chichuka.birdvgvardc.ror.data.utils

import android.util.Log
import com.chichuka.birdvgvardc.ror.presentation.app.BirdGuardApp
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BirdGuardPushToken {

    suspend fun chickenGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume("")
                    Log.d(BirdGuardApp.Companion.TOWNPLANNER_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(BirdGuardApp.Companion.TOWNPLANNER_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}