package com.chichuka.birdvgvardc.ror.domain.model

import com.google.gson.annotations.SerializedName


data class BirdGuardEntity (
    @SerializedName("ok")
    val chickenOk: String,
    @SerializedName("url")
    val chickenUrl: String,
    @SerializedName("expires")
    val chickenExpires: Long,
)