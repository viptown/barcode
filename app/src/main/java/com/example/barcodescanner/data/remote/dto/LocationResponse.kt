package com.example.barcodescanner.data.remote.dto
import kotlinx.serialization.Serializable

@Serializable
data class LocationResponse(
    val position_id: String,
    val position_name: String
)
//@Serializable
//data class LocationResponse(
//    val body: String,
//    val title: String,
//    val id: Int,
//    val userId: Int
//)
