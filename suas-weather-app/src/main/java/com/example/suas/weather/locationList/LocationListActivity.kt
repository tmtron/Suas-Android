package com.example.suas.weather.locationList

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.suas.weather.R
import com.example.suas.weather.WeatherApplication
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.search.AddLocationActivity
import com.example.suas.weather.suas.StateModels
import kotlinx.android.synthetic.main.activity_location_list.*
import zendesk.suas.CombinedSubscription
import zendesk.suas.Store
import zendesk.suas.Subscription

class LocationListActivity : AppCompatActivity() {

    private val store: Store by lazy { WeatherApplication.from(this).store }
    private val service: WeatherService by lazy { WeatherApplication.from(this).weatherService }

    private lateinit var subscriptions: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)
        setActionBar(toolbar)

        addLocation.setOnClickListener {
            startActivity(Intent(this@LocationListActivity, AddLocationActivity::class.java))
        }

        val listComponent = ListComponent(locationList, service, store)
        val weatherComponent = WeatherComponent(weatherIcon, locationTemperature, weather, precip, wind)
        val progressComponent = ProgressComponent(locationProgressbar)

        subscriptions = CombinedSubscription.from(
                store.addListener(StateModels.Locations::class.java, listComponent),
                store.addListener(weatherComponent.selector, weatherComponent),
                store.addListener(StateModels.Progress::class.java, progressComponent)
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