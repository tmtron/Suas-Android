package com.example.suas.weather.search

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.suas.weather.R
import com.example.suas.weather.suas.AddLocation
import com.example.suas.weather.suas.StateModels
import zendesk.suas.Dispatcher
import zendesk.suas.Listener

class ListComponent(recyclerView: RecyclerView, dispatcher: Dispatcher) : Listener<StateModels.FoundLocations> {

    private val adapter: LocationAdapter = LocationAdapter(this, dispatcher)
    private var list: List<ListItem> = listOf()

    init {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    override fun update(e: StateModels.FoundLocations) {
        list = e.foundLocation.map { ListItem(it, it.name.hashCode().toLong()) }
        adapter.notifyDataSetChanged()
    }

    fun forPosition(position: Int): ListItem = list[position]

    fun listSize(): Int = list.size

    class LocationAdapter(val listComponent: ListComponent, val dispatcher: Dispatcher) : RecyclerView.Adapter<LocationViewHolder>() {

        init {
            setHasStableIds(true)
        }

        override fun onBindViewHolder(holder: LocationViewHolder?, position: Int) {
            holder?.bind(listComponent.forPosition(position), dispatcher)
        }

        override fun getItemId(position: Int): Long {
            return listComponent.forPosition(position).id
        }

        override fun getItemCount(): Int = listComponent.listSize()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_search, parent, false)
            return LocationViewHolder(view)
        }
    }

    data class ListItem(val location: StateModels.Location, val id: Long)

    class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: ListItem, dispatcher: Dispatcher) {
            view.findViewById<TextView>(R.id.searchItemLabel).text = item.location.name
            view.setOnClickListener {
                dispatcher.dispatch(AddLocation(item.location))
                (it.context as? Activity)?.finish()
            }
        }

    }

}