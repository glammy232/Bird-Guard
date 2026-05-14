package com.chichuka.birdvgvardc.ror.domain.model

import com.google.gson.annotations.SerializedName


private const val TP_A = "com.chichuka.birdvgvardc"
data class TownPlannerParam (
    @SerializedName("af_id")
    val chickenAfId: String,
    @SerializedName("bundle_id")
    val chickenBundleId: String = TP_A,
    @SerializedName("os")
    val chickenOs: String = "Android",
    @SerializedName("store_id")
    val chickenStoreId: String = TP_A,
    @SerializedName("locale")
    val chickenLocale: String,
    @SerializedName("push_token")
    val chickenPushToken: String,
    @SerializedName("firebase_project_id")
    val chickenFirebaseProjectId: String = "farm-craft-dacf4"

    )