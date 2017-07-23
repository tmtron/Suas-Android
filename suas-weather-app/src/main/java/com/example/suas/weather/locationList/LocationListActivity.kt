package com.example.suas.weather.locationList

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.suas.weather.R
import com.example.suas.weather.Subscription
import com.example.suas.weather.WeatherApplication
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.search.AddLocationActivity
import com.zendesk.suas.Store
import kotlinx.android.synthetic.main.activity_location_list.*

class LocationListActivity : AppCompatActivity() {

    private val store: Store by lazy { WeatherApplication.from(this).store }
    private val service: WeatherService by lazy { WeatherApplication.from(this).weatherService }

    private lateinit var subscriptions: List<Subscription>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)
        setActionBar(toolbar)

        addLocation.setOnClickListener {
            startActivity(Intent(this@LocationListActivity, AddLocationActivity::class.java))
        }

        subscriptions = listOf(
                ListComponent(locationList, service, store),
                WeatherComponent(weatherIcon, locationTemperature, weather, precip, wind),
                ProgressComponent(locationProgressbar)
        )
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