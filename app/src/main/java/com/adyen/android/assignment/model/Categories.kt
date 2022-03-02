package com.adyen.android.assignment.model

/**
 *
 *  [Categories] combines the categories offered by FourSquare Places API into 5 categories
 *  Since each general category id in Places API has unique id sequence, we can filter these accordingly
 *
 *  Travel: Event(14k), Arts & Entertainment(10k)
 *  Health: Medicine(15k)
 *  Business: Community & Government(12), Business & Professional Services(11)
 *  Restaurants: Dining and Drinking(13k),
 *  Outdoor: Landmarks and Outdoors(16k), Retail(17k), Sport and Recreation(18), Travel & Transportation(19)
 *
 */

object Categories {
    const val RESTAURANT = "Restaurants"
    const val HEALTH = "Health"
    const val TRAVEL = "Travel"
    const val BUSINESS = "Business"
    const val OUTDOOR = "Outdoor"

//    val allValues = listOf(
//        CategoryItem(HOSPITAL, R.drawable.ic_hospital),
//        CategoryItem(PICNIC, R.drawable.ic_tour),
//        CategoryItem(BANK, R.drawable.ic_bank),
//        CategoryItem(RESTAURANT, R.drawable.ic_restaurant),
//        CategoryItem(UNIVERSITY, R.drawable.ic_university)
//    )

    /**
     * Since the 3 right most digits of [id] represent subcategories, we can get rid of it
     * as we only need the first two to uniquely identify category
     */
    fun getById(id: Int): String {
        return when(id / 1000) {
            13 -> RESTAURANT
            15 -> HEALTH
            10, 14 -> TRAVEL
            11, 12 -> BUSINESS
            16, 17, 18, 19 -> OUTDOOR
            else -> throw IllegalArgumentException("Place Category does not match!")
        }
    }
}