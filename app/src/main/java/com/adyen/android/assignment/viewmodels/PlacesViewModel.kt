package com.adyen.android.assignment.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.api.VenueRecommendationsQueryBuilder
import com.adyen.android.assignment.model.Categories
import com.adyen.android.assignment.model.PlaceState
import com.adyen.android.assignment.api.model.ResponseWrapper
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.api.PlacesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IndexOutOfBoundsException
import java.lang.RuntimeException

class PlacesViewModel : ViewModel() {

    private val _state = MutableStateFlow<PlaceState>(PlaceState.Idle)
    val state: StateFlow<PlaceState> get() = _state

    fun getFilteredPlaces(category: String): List<Result> {
        val placeList = _state.value as PlaceState.Result

        return placeList.places?.filter { result ->
            try {
                val id = result.categories[0].id.toInt()
                if(Categories.getById(id) == category)
                    return@filter true
            } catch (e: IndexOutOfBoundsException ) {
                // Move on to next item
            } catch (parseException: RuntimeException) {
                parseException.printStackTrace()
            }
            false
        } ?: listOf()
    }

    fun getPlaces() {
        // Make sure when user quits during Network request, the request should get cancelled
        // with viewModel's scope
        viewModelScope.launch {

            _state.value = PlaceState.Loading

            val query = VenueRecommendationsQueryBuilder()
                .setLatitudeLongitude(52.376510, 4.905890)
                .build()

            PlacesService.instance
                .getVenueRecommendations(query)
                .enqueue(object : Callback<ResponseWrapper> {
                    override fun onResponse(
                        call: Call<ResponseWrapper>,
                        response: Response<ResponseWrapper>
                    ) {

                        if (response.isSuccessful) {
                            _state.value = PlaceState.Result(response.body()?.results)
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