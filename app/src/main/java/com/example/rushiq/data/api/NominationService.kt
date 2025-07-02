package com.example.rushiq.data.api

import com.example.rushiq.data.models.location.NominationResponse
import com.google.firebase.auth.FirebaseAuth
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NominationService {
 @GET("reverse")
 suspend fun reverseGeocode(
  @Query("lat") latitude : Double,
  @Query("lon") longitude : Double,
  @Query("Format") format : String  = "json",
  @Query("addressdetails") addressDetails : Int  = 1,
  @Header("User-Agent") userAgent : String = "Zepto/1.0 (nakulcharak280@gmail.com)"
 ): NominationResponse
 @GET("search")
 suspend fun searchNearby(
  @Query("q") query : String,
  @Query("Format") format : String  = "json",
  @Query("addressdetails") addressDetails : Int  = 1,
  @Query("limit") limit : Int  = 5,
  @Header("User-Agent") userAgent : String = "Zepto/1.0 (nakulcharak280@gmail.com)"
 ):List<NominationResponse>

 @GET("search")
 suspend fun searchInBoundingBox(
  @Query("q") query : String,
  @Query("Format") format : String  = "json",
  @Query("addressdetails") addressDetails : Int  = 1,
  @Query("viewbox") viewbox : String,
  @Query("bounded") bounded : String ="1",
  @Header("User-Agent") userAgent : String = "Zepto/1.0 (nakulcharak280@gmail.com)"
 ): List<NominationResponse>
}