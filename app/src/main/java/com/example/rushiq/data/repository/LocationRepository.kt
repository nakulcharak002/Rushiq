package com.example.rushiq.data.repository

import com.example.rushiq.data.models.location.NominationResponse

interface LocationRepository {
    suspend  fun getAddressFromCoordinates(latitude : Double , longitude : Double):Result <NominationResponse>
    suspend fun searchNearbyPlaces(query:String , limit : Int = 5):Result<List<NominationResponse>>
    suspend fun searchInArea(query: String, minLon:Double , minLat: Double , maxLon :Double , maxLat : Double ):Result<List<NominationResponse>>

}