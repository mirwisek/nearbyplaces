package com.adyen.android.assignment

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng

val Context.sharedPrefs: SharedPreferences
    get() = getSharedPreferences("PlacesApp.SharedPrefs", Context.MODE_PRIVATE)

fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)
fun Location.stringValue() = "$latitude,$longitude"

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

fun Fragment.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(msg, duration)
}

fun log(msg: String, tag: String = "ffnet") {
    Log.d(tag, msg)
}