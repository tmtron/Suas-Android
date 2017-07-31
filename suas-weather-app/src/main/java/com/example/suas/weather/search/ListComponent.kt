package com.example.suas.weather.search

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.suas.weather.R
import com.example.suas.weather.Subscription
import com.example.suas.weather.suas.AddLocation
import com.example.suas.weather.suas.StateModels
import zendesk.suas.Component
import zendesk.suas.Selector
import zendesk.suas.Store
import zendesk.suas.StoreApi

class ListComponent(recyclerView: RecyclerView, dispatcher: StoreApi) : Component<StateModels.FoundLocations, List<ListComponent.ListItem>>, Subscription {

    private val adapter: LocationAdapter = LocationAdapter(this, dispatcher)
    private var list: List<ListItem> = listOf()

    init {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    override fun update(e: List<ListItem>) {
        list = e
        adapter.notifyDataSetChanged()
    }

    override fun getSelector(): Selector<StateModels.FoundLocations, List<ListItem>> = Selector {
        it.foundLocation.map { ListItem(it, it.name.hashCode().toLong()) }
    }

    fun forPosition(position: Int): ListItem = list[position]

    fun listSize(): Int = list.size

    override fun disconnect(store: Store) {
        store.disconnect(this)
    }

    override fun connect(store: Store) {
        store.connect(this, StateModels.FoundLocations::class.java)
    }

    class LocationAdapter(val listComponent: ListComponent, val dispatcher: StoreApi) : RecyclerView.Adapter<LocationViewHolder>() {

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

        fun bind(item: ListItem, dispatcher: StoreApi) {
            view.findViewById<TextView>(R.id.searchItemLabel).text = item.location.name
            view.setOnClickListener {
                dispatcher.dispatchAction(AddLocation(item.location))
                (it.context as? Activity)?.finish()
            }
        }

    }

}