package com.adyen.android.assignment.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.ActivityMainBinding
import com.adyen.android.assignment.log
import com.adyen.android.assignment.model.PlaceState
import com.adyen.android.assignment.viewmodels.PlacesViewModel

class MainActivity : FragmentActivity() {

    private lateinit var viewModelPlaces: PlacesViewModel
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    var mapListener: OnPlaceItemClickForMapListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelPlaces = ViewModelProvider(this)[PlacesViewModel::class.java]

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.apply {
            // Since we have map in the viewPager, we disable scrolling pages through swipes,
            // because user can do it with BottomNavigationView
            viewPager.isUserInputEnabled = false
            // Keep both the fragments pre-loaded
            viewPager.offscreenPageLimit = 2
            viewPager.adapter = viewPagerAdapter

            navView.setOnItemSelectedListener { menuItem ->
                viewPager.currentItem = when (menuItem.itemId) {
                    R.id.nearby_places -> 0
                    R.id.maps -> 1
                    else -> 0
                }
                return@setOnItemSelectedListener true
            }
        }

        // Retrieve data only once, don't repeat on configuration changes
        if(viewModelPlaces.state.value is PlaceState.Idle)
            viewModelPlaces.getPlaces()
    }

    fun onPlaceClicked(placeResult: Result) {
        binding.navView.selectedItemId = R.id.maps
        mapListener?.showOnMap(placeResult)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}
