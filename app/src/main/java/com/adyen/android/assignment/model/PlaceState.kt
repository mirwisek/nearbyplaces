package com.adyen.android.assignment.model


sealed class PlaceState {
    object Idle: PlaceState()
    object Loading: PlaceState()
    data class Result(val places: List<com.adyen.android.assignment.api.model.Result>?): PlaceState()
    data class Error(val error: Throwable?): PlaceState()

}
