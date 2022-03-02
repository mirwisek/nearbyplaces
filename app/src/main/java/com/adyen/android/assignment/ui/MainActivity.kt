package com.adyen.android.assignment.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.adyen.android.assignment.viewmodels.PlacesViewModel
import com.adyen.android.assignment.R
import com.adyen.android.assignment.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {

    private val viewModelPlaces: PlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.apply {
            viewPager.adapter = viewPagerAdapter

            navView.setOnItemSelectedListener { menuItem ->
                viewPager.currentItem = when(menuItem.itemId) {
                    R.id.restaurants -> 0
                    R.id.health -> 1
                    R.id.travel -> 2
                    R.id.business -> 3
                    R.id.outdoor -> 4
                    else -> 0
                }
                return@setOnItemSelectedListener true
            }
        }

        viewModelPlaces.getPlaces()
    }


}
