package com.example.barcodescanner.data.remote

import com.example.barcodescanner.data.remote.dto.LocationRequest
import com.example.barcodescanner.data.remote.dto.LocationResponse
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

interface LocationService {

    suspend fun getLocations():List<LocationResponse>

    suspend fun createLocation(postLocationRequest: LocationRequest):LocationResponse?

    companion object{
        fun create(): LocationService{
            return LocationServiceImpl (
                client = HttpClient(Android){
                    install(Logging){
                        level = LogLevel.ALL
                    }
                    install(JsonFeature){
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}