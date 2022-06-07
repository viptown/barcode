package com.example.barcodescanner.data.remote

import android.util.Log
import com.example.barcodescanner.data.remote.dto.LocationRequest
import com.example.barcodescanner.data.remote.dto.LocationResponse
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class LocationServiceImpl(
    private val client:HttpClient
) :LocationService{

    override suspend fun getLocations(): List<LocationResponse> {
        return try{
            client.get { url(HttpRoutes.POSTS) }
        }catch (e:RedirectResponseException){
            println("Error: ${e.response.status.description}")
            emptyList()
        }catch (e:ClientRequestException){
            println("Error: ${e.response.status.description}")
            emptyList()
        }catch (e:ServerResponseException){
            println("Error: ${e.response.status.description}")
            emptyList()
        }catch (e:Exception){
            println("Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun createLocation(postLocationRequest: LocationRequest): LocationResponse? {
        return try {
            client.post<LocationResponse>() {
                url(HttpRoutes.PUTS)
                contentType(ContentType.Application.Json)
                body = postLocationRequest
            }
        } catch (e: RedirectResponseException) {
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }

    }
}