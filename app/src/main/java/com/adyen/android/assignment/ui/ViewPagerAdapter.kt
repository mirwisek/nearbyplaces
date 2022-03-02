package com.adyen.android.assignment.ui


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.adyen.android.assignment.R

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val titles = arrayOf(
        R.string.title_restaurants,
        R.string.title_health,
        R.string.title_travel,
        R.string.title_business,
        R.string.title_outdoor
    )

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): Fragment =
        HomeFragment.newInstance(titles[position])

}