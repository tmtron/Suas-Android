package com.example.suas.weather.search

import android.app.Activity
import android.os.Bundle
import com.example.suas.weather.R
import com.example.suas.weather.WeatherApplication
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.suas.ResetSuggestedCities
import com.example.suas.weather.suas.StateModels
import kotlinx.android.synthetic.main.activity_add_location.*
import zendesk.suas.CombinedSubscription
import zendesk.suas.Store
import zendesk.suas.Subscription

class AddLocationActivity : Activity() {

    private val store: Store by lazy { WeatherApplication.from(this).store }
    private val service: WeatherService by lazy { WeatherApplication.from(this).weatherService }

    private lateinit var subscriptions: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        setActionBar(toolbar)
        toolbarBackButton.setOnClickListener{ onBackPressed() }

        val searchBox = SearchBoxComponent(searchForCity, store, service)
        val noResult = NoResultsComponent(noLocationsFoundLabel)
        val progress = ProgressComponent(locationProgressbar)
        val listComponent = ListComponent(locationList, store)

        // clear data from a previous search
        store.dispatch(ResetSuggestedCities())

        subscriptions = CombinedSubscription.from(
                store.addListener(StateModels.Progress::class.java, searchBox),
                store.addListener(noResult.selector, noResult),
                store.addListener(StateModels.Progress::class.java, progress),
                store.addListener(StateModels.FoundLocations::class.java, listComponent)
        )
        subscriptions.informWithCurrentState()
    }

    override fun onResume() {
        super.onResume()
        subscriptions.addListener()
        subscriptions.informWithCurrentState()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.removeListener()
    }

}
