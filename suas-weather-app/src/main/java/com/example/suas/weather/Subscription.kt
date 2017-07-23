package com.example.suas.weather;

import com.zendesk.suas.Store;

interface Subscription {
    fun connect(store: Store)
    fun disconnect(store: Store)
}
