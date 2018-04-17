package com.example.suas.weather.locationList

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.suas.weather.R
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.suas.Location
import com.example.suas.weather.suas.StateModels
import zendesk.suas.Listener
import zendesk.suas.Dispatcher

typealias LocationSelected = (location: Location) -> Unit

class ListComponent(recyclerView: RecyclerView, weatherService: WeatherService, dispatcher: Dispatcher)
    : Listener<StateModels.Locations> {

    private var list: List<Location> = listOf()
    private val adapter = LocationAdapter(this, { location ->
        dispatcher.dispatch(com.example.suas.weather.suas.LocationSelected(location))
        dispatcher.dispatch(weatherService.loadWeather(location))
    })

    init {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    override fun update(e: StateModels.Locations) {
        list = e.locations
        adapter.notifyDataSetChanged()
    }


    fun forPosition(position: Int): Location = list[position]

    fun listSize(): Int = list.size

    class LocationAdapter(val listComponent: ListComponent, val listener: LocationSelected) : RecyclerView.Adapter<LocationViewHolder>() {

        override fun onBindViewHolder(holder: LocationViewHolder?, position: Int) {
            holder?.bind(listComponent.forPosition(position), listener)
        }

        override fun getItemCount(): Int {
            return listComponent.listSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
            val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.view_search, parent, false)
            return LocationViewHolder(view)
        }

    }

    class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(location: Location, listener: LocationSelected) {
            itemView.findViewById<TextView>(R.id.searchItemLabel).text = location.name
            itemView.setOnClickListener { listener(location) }
        }
    }
}