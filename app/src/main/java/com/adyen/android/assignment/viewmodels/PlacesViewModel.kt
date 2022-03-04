package com.adyen.android.assignment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.model.PlaceState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlacesViewModel : ViewModel() {

    private val _state = MutableStateFlow<PlaceState>(PlaceState.Idle)
    val state: StateFlow<PlaceState> get() = _state

    fun getPlaces(location: LatLng) {
        // Make sure when user quits during Network request, the request should get cancelled
        // with viewModel's scope
        viewModelScope.launch {

            _state.value = PlaceState.Loading

            val query = VenueRecommendationsQueryBuilder()
                .setLatitudeLongitude(location.latitude, location.longitude)
                .build()

            PlacesService.instance
                .getVenueRecommendations(query)
                .enqueue(object : Callback<ResponseWrapper> {
                    override fun onResponse(
                        call: Call<ResponseWrapper>,
                        response: Response<ResponseWrapper>
                    ) {
                        if (response.isSuccessful) {
                            val sortedList = response.body()?.results?.sortedBy { it.distance }
                            _state.value = PlaceState.Result(sortedList)
                        } else {
                            _state.value = PlaceState.Error(Exception(response.message()))
                        }

                    }

                    override fun onFailure(call: Call<ResponseWrapper>, t: Throwable) {
                        _state.value = PlaceState.Error(t)
                    }
                })
        }
    }

}