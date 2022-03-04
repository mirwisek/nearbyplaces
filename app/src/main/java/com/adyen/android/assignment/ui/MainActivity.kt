package com.adyen.android.assignment.ui

import android.location.Location
import android.os.Bundle
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.adyen.android.assignment.*
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.ActivityMainBinding
import com.adyen.android.assignment.model.PlaceState
import com.adyen.android.assignment.ui.adapters.ViewPagerAdapter
import com.adyen.android.assignment.viewmodels.PlacesViewModel

class MainActivity : FragmentActivity() {

    private lateinit var viewModelPlaces: PlacesViewModel
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val KEY_CURRENT_LOCATION = "location.current"
    }

    var mapListener: OnPlaceItemClickForMapListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelPlaces = ViewModelProvider(this)[PlacesViewModel::class.java]

        /**
         * Receive the location, provided by [LocationActivity]
         */
        val location = intent.getParcelableExtra<Location>(KEY_CURRENT_LOCATION)
        // Very unlikely to occur, but when it does, we should safely exit the application
        if (location == null) {
            toast("Could not determine your location!")
            finish()
        } else {
            sharedPrefs.edit(true) {
                putString(KEY_CURRENT_LOCATION, location.stringValue())
            }
        }

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.apply {
            // Since we have map in the viewPager, we disable scrolling pages through swipes,
            // because user can do it with BottomNavigationView
            viewPager.isUserInputEnabled = false
            // Keep both the fragments pre-loaded
            viewPager.offscreenPageLimit = viewPagerAdapter.itemCount
            viewPager.adapter = viewPagerAdapter

            navView.setOnItemSelectedListener { menuItem ->
                viewPager.currentItem = when (menuItem.itemId) {
                    R.id.maps -> 1
                    else -> 0
                }
                return@setOnItemSelectedListener true
            }
        }

        // Retrieve data only once, don't repeat on configuration changes
        if (viewModelPlaces.state.value is PlaceState.Idle)
            viewModelPlaces.getPlaces(location!!.toLatLng())
    }

    fun onPlaceClicked(placeResult: Result) {
        binding.navView.selectedItemId = R.id.maps
        mapListener?.showOnMap(placeResult)
    }

}
