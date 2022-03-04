package com.adyen.android.assignment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.adyen.android.assignment.*
import com.adyen.android.assignment.R
import com.adyen.android.assignment.databinding.ActivityLocationBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar

@SuppressLint("MissingPermission")
class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedApi: FusedLocationProviderClient

    private val permissions: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val locationRequest = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 100
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrElse(Manifest.permission.ACCESS_FINE_LOCATION) { false } -> {
                onLocationPermissionApproved()
            }
            else -> {
                onLocationPermissionDeclined()
            }
        }
    }

    private val gpsEnableRequest = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_CANCELED -> {
                toast("Couldn't enable GPS")
                binding.btnLocation.enable()
            }
            else -> {
                onGPSEnabled()
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            binding.progress.gone()
            val location = result.locations[0]
            fusedApi.removeLocationUpdates(this)
            navigateToMainActivity(location)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        fusedApi = LocationServices.getFusedLocationProviderClient(this)
        if (hasPermissions(*permissions)) {
            retrieveLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLocation.setOnClickListener {
            binding.btnLocation.disable()
            if (hasPermissions(*permissions)) {
                retrieveLocation(false)
            } else {    // Request Location Permission
                locationPermissionRequest.launch(permissions)
            }
        }
    }


    private fun onGPSEnabled() {
        binding.progress.visible()
        // Since all pre-conditions are met, now we can request location
        fusedApi.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun onLocationPermissionApproved() {
        // Permission request was initiated by Button Click make sure after the grant of permission
        // the user intended action takes place
        retrieveLocation(false)
    }

    private fun onLocationPermissionDeclined() {
        Snackbar.make(
            binding.root, getString(R.string.location_declined_message),
            Snackbar.LENGTH_LONG
        ).show()
        binding.btnLocation.enable()
    }

    private fun navigateToMainActivity(location: Location) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.KEY_CURRENT_LOCATION, location)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    /**
     * Uses lastLocation if available otherwise, enforces the user to enable GPS
     * and requests current location
     */
    private fun retrieveLocation(tryOnlyLastLocation: Boolean = true) {
        fusedApi.lastLocation.addOnCompleteListener { task ->
            if (task.result == null) {
                // Don't enforce getting location if lastLocation failed
                if (!tryOnlyLastLocation) {
                    // Enable GPS to get current location, if turned off
                    if (isGPSEnabled())
                        onGPSEnabled()
                    else
                        enableGPS()
                }
            } else {
                navigateToMainActivity(task.result)
            }
        }
    }

    private fun hasPermissions(vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * Checks if GPS location is ON
     */
    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * Helps user enable GPS using a Dialog without having to go to Settings
     */
    private fun enableGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        // This shows the Alert Dialog to display user to enable GPS
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    gpsEnableRequest.launch(
                        IntentSenderRequest.Builder(exception.resolution).build()
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
}