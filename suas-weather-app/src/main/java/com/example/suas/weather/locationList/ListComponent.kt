package com.example.suas.weather.locationList

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.suas.weather.R
import com.example.suas.weather.Subscription
import com.example.suas.weather.network.WeatherService
import com.example.suas.weather.suas.StateModels
import com.zendesk.suas.Component
import com.zendesk.suas.Dispatcher
import com.zendesk.suas.Selector
import com.zendesk.suas.Store

typealias LocationSelected = (location: StateModels.Location) -> Unit

class ListComponent(recyclerView: RecyclerView, weatherService: WeatherService, dispatcher: Dispatcher)
    : Component<StateModels.Locations, List<StateModels.Location>>, Subscription {

    private var list: List<StateModels.Location> = listOf()
    private val adapter = LocationAdapter(this, { location ->
        dispatcher.dispatchAction(com.example.suas.weather.suas.LocationSelected(location))
        dispatcher.dispatchAction(weatherService.loadWeather(location))
    })

    init {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    override fun update(e: List<StateModels.Location>) {
        list = e
        adapter.notifyDataSetChanged()
    }

    override fun getSelector(): Selector<StateModels.Locations, List<StateModels.Location>> = Selector {
        it.locations
    }

    fun forPosition(position: Int): StateModels.Location = list[position]

    fun listSize(): Int = list.size

    override fun disconnect(store: Store) {
        store.disconnect(this)
    }

    override fun connect(store: Store) {
        store.connect(this, StateModels.Locations::class.java)
    }

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

        fun bind(location: StateModels.Location, listener: LocationSelected) {
            itemView.findViewById<TextView>(R.id.searchItemLabel).text = location.name
            itemView.setOnClickListener { listener(location) }
        }
    }
}