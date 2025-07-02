package com.example.rushiq.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.rushiq.data.api.NominationService
import com.example.rushiq.data.models.location.NominationResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val nominationService: NominationService,
    @ApplicationContext private val context: Context
) : LocationRepository {

    // Used for converting objects to/from JSON
    private val gson = Gson()

    // SharedPreferences to store location cache on device
    private val prefs: SharedPreferences = context.getSharedPreferences(
        LOCATION_CACHE, Context.MODE_PRIVATE
    )

    // MutableStateFlow to hold cached location
    private val _cachedLocation = MutableStateFlow<NominationResponse?>(null)
      val cachedLocation: StateFlow<NominationResponse?> = _cachedLocation

    init {
        loadCachedLocation()
    }

    // Loads saved location data (if any) from SharedPreferences
    private fun loadCachedLocation() {
        val cachedJson = prefs.getString(LOCATION_KEY, null)
        if (!cachedJson.isNullOrEmpty()) {
            try {
                val cachedResponse = gson.fromJson(cachedJson, NominationResponse::class.java)
                _cachedLocation.value = cachedResponse
            } catch (e: Exception) {
                // Ignore malformed cache
            }
        }
    }

    // Saves location data into SharedPreferences and updates StateFlow
    private fun cacheLocation(response: NominationResponse) {
        try {
            val json = gson.toJson(response)
            prefs.edit().putString(LOCATION_KEY, json).apply()
            _cachedLocation.value = response
        } catch (e: Exception) {
            // Ignore caching failure
        }
    }

    // Gets address from lat/lon using API (or cached if available)
    override suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<NominationResponse> {
        _cachedLocation.value?.let {
            return Result.success(it)
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = nominationService.reverseGeocode(latitude, longitude)
                cacheLocation(response)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Calls the API to search nearby places by a query
    override suspend fun searchNearbyPlaces(
        query: String,
        limit: Int
    ): Result<List<NominationResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = nominationService.searchNearby(query, limit = limit)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    override suspend fun searchInArea(
        query: String,
        minLon: Double,
        minLat: Double,
        maxLon: Double,
        maxLat: Double
    ): Result<List<NominationResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val viewBoxParam = "$minLon,$minLat,$maxLon,$maxLat"
                val response = nominationService.searchInBoundingBox(query , viewbox = viewBoxParam)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    companion object {
        private const val LOCATION_KEY = "user_cache"
        private const val LOCATION_CACHE = "location_cache"
    }
}
