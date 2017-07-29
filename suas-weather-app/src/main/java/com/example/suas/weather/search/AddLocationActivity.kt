package com.example.suas.weather.search

import android.app.Activity
import android.os.Bundle
import com.example.suas.weather.R
import com.example.suas.weather.Subscription
import com.example.suas.weather.WeatherApplication
import com.example.suas.weather.network.WeatherService
import kotlinx.android.synthetic.main.activity_add_location.*
import zendesk.suas.Store

class AddLocationActivity : Activity() {

    private val store: Store by lazy { WeatherApplication.from(this).store }
    private val service: WeatherService by lazy { WeatherApplication.from(this).weatherService }

    private lateinit var subscriptions: List<Subscription>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        setActionBar(toolbar)
        toolbarBackButton.setOnClickListener{ onBackPressed() }

        subscriptions = listOf(
                SearchBoxComponent(searchForCity, store, service),
                NoResultsComponent(noLocationsFoundLabel),
                ProgressComponent(locationProgressbar),
                ListComponent(locationList, store))
    }

    override fun onResume() {
        super.onResume()
        subscriptions.forEach { it.connect(store) }
    }

    override fun onPause() {
        super.onPause()
        subscriptions.forEach { it.disconnect(store) }
    }

}

