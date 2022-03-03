package com.adyen.android.assignment.ui

import com.adyen.android.assignment.api.model.Result

interface OnPlaceItemClickForMapListener {
    fun showOnMap(placeItem: Result)
}