package com.s.newsapp.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.s.newsapp.MainActivity
import org.junit.Before
import org.junit.Rule
import com.s.newsapp.R
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class feedFragmentTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    val itemInTest = 1

    @Before
    fun setUp() {
        Intents.init();
    }

    @Test
    fun test_isCategoryItemsVisibleOnAppLaunch() {
        Espresso.onView(withId(R.id.rvCategory))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }




}