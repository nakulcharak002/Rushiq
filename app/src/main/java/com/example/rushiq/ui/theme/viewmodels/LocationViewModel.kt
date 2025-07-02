package com.example.rushiq.ui.theme.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.models.location.NominationResponse
import com.example.rushiq.data.repository.LocationRepository
import com.example.rushiq.data.repository.LocationRepositoryImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _osmResponse = MutableStateFlow<NominationResponse?>(null)
    val osmResponse: StateFlow<NominationResponse?> = _osmResponse.asStateFlow()

    private val _nearbyPlaces = MutableStateFlow<List<NominationResponse>>(emptyList())
    val nearbyPlaces: StateFlow<List<NominationResponse>> = _nearbyPlaces.asStateFlow()

    private val _userAddress = MutableStateFlow("")
    val userAddress: StateFlow<String> = _userAddress.asStateFlow()

    private val _deliveryTime = MutableStateFlow("6 Mins")
    val deliveryTime: StateFlow<String> = _deliveryTime.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private var cacheLoaded = false

    init {
        viewModelScope.launch {
            (locationRepository as? LocationRepositoryImpl)?.cachedLocation?.collect { cachedResponse ->
                cachedResponse?.let {
                    _osmResponse.value = it
                    _userAddress.value = it.display_name ?: "LOCATION FOUND"
                    updateDeliveryTime(it)
                    cacheLoaded = true
                }
            }
        }
    }

    fun updateUserLocation() {
        val showLoadingIndicator = !cacheLoaded || _userAddress.value.isNotEmpty()

        viewModelScope.launch {
            if (showLoadingIndicator) _isLoading.value = true
            _error.value = null

            try {
                val location = getCurrentLocation()
                if (location != null) {
                    _currentLocation.value = location

                    locationRepository.getAddressFromCoordinates(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ).onSuccess { response ->
                        _osmResponse.value = response
                        _userAddress.value = response.display_name ?: "Location Found"
                        updateDeliveryTime(response)
                        searchNearByPlaces("amenity")
                    }.onFailure { exception ->
                        _error.value = "Could not get address: ${exception.message}"
                    }
                } else {
                    _error.value = "Could not get current location"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateDeliveryTime(response: NominationResponse) {
        _deliveryTime.value = "6 Mins"
    }

    private fun searchNearByPlaces(query: String, limit: Int = 5) {
        viewModelScope.launch {
            _currentLocation.value?.let { location ->
                val lat = location.latitude
                val lon = location.longitude

                val boundingBox = calculateBoundingBox(lat, lon, 1.0)

                locationRepository.searchInArea(
                    query = query,
                    minLon = boundingBox.minLon,
                    minLat = boundingBox.minLat,
                    maxLon = boundingBox.maxLon,
                    maxLat = boundingBox.maxLat
                ).onSuccess { places ->
                    _nearbyPlaces.value = places
                }.onFailure { exception ->
                    _error.value = "Failed to find nearby places: ${exception.message}"
                }
            } ?: run {
                _error.value = "Location not available"
            }
        }
    }

    private fun calculateBoundingBox(
        lat: Double,
        lon: Double,
        radiusKm: Double
    ): BoundingBox {
        val latChange = radiusKm / 111.0
        val lonChange = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)))

        val minLat = lat - latChange
        val maxLat = lat + latChange
        val minLon = lon - lonChange
        val maxLon = lon + lonChange

        return BoundingBox(minLon, minLat, maxLon, maxLat)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cancellationTokenSource = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).await()
            } else {
                _error.value = "Location permission not granted"
                null
            }
        } catch (e: Exception) {
            _error.value = "Location error: ${e.message}"
            null
        }
    }
}

// Data class for Bounding Box
data class BoundingBox(
    val minLon: Double,
    val minLat: Double,
    val maxLon: Double,
    val maxLat: Double
)
