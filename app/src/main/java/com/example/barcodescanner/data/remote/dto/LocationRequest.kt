package com.example.barcodescanner.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationRequest(
    val hbl: String,
    val position_id: String
)
//@Serializable
//data class LocationRequest(
//    val body: String,
//    val title: String,
//    val userId: Int
//)
