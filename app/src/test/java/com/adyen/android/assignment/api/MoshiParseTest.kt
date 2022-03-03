package com.adyen.android.assignment.api

import com.adyen.android.assignment.api.model.Result
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MoshiParseTest {

    private lateinit var json: String

    @Before
    fun setUp() {
        json = """
            {
            "fsq_id": "4d3d2bd1a2e4b1f71ab3ef25",
            "categories": [
                {
                    "id": 11042,
                    "name": "Financial Service",
                    "icon": {
                        "prefix": "https://ss3.4sqi.net/img/categories_v2/shops/financial_",
                        "suffix": ".png"
                    }
                }
            ],
            "chains": [],
            "distance": 22,
            "geocodes": {
                "main": {
                    "latitude": 52.37669772667794,
                    "longitude": 4.906015805762289
                }
            },
            "location": {
                "address": "Simon Carmiggeltstraat 6/50",
                "country": "NL",
                "formatted_address": "Simon Carmiggeltstraat 6/50, 1011 DJ Amsterdam",
                "locality": "Amsterdam",
                "neighborhood": [
                    "Nieuwmarkt/Lastage"
                ],
                "postcode": "1011 DJ",
                "region": "North Holland"
            },
            "name": "Adyen BV",
            "related_places": {},
            "timezone": "Europe/Amsterdam"
        }
        """.trimIndent()
    }

    /**
     * Although Moshi has already been tested, but here I am ensuring that
     * the data classes are working as expected
     *
     * Note: Through this test, I figured the initial given code template by Adyen had type in [Result.geocodes]
     * that I refactored to match with API
     */

    @Test
    fun shouldParseLocationFromJsonStringToDouble() {
        val moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<Result> = moshi.adapter(Result::class.java)

        val response = adapter.fromJson(json)

        val location = response?.geocodes?.main
        Assert.assertEquals(location?.latitude, 52.37669772667794)
        Assert.assertEquals(location?.longitude, 4.906015805762289)
        Assert.assertEquals(response?.name, "Adyen BV")
        Assert.assertEquals(response?.location?.address, "Simon Carmiggeltstraat 6/50")
        Assert.assertEquals(response?.distance, 22)
        val categories = response?.categories
        Assert.assertNotNull(categories)
        assert(categories!!.isNotEmpty())
    }

}