package com.example.suas.weather

import zendesk.suas.Store

interface Subscription {
    fun connect(store: Store)
    fun disconnect(store: Store)
}
