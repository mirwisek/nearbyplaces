package com.adyen.android.assignment.model

import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * Since we have generalized the sub categories into 5 general categories,
 * let's make sure that our logic is solid
 */
class CategoriesUnitTest {

    @Test
    fun shouldMatchValidCategories_AfterArithmetic() {
        assertEquals(Categories.getById(11_001), Categories.BUSINESS)
        assertEquals(Categories.getById(12_011), Categories.BUSINESS)
        assertEquals(Categories.getById(15_031), Categories.HEALTH)
        assertEquals(Categories.getById(16_054), Categories.OUTDOOR)
        assertEquals(Categories.getById(17_139), Categories.OUTDOOR)
        assertEquals(Categories.getById(18_000), Categories.OUTDOOR)
        assertEquals(Categories.getById(19_056), Categories.OUTDOOR)
        assertEquals(Categories.getById(14_001), Categories.TRAVEL)
        assertEquals(Categories.getById(13_380), Categories.RESTAURANT)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionForNegativeValue() {
        Categories.getById(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionForZero() {
        Categories.getById(0)
    }

}