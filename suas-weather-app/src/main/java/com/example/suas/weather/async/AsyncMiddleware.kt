package com.example.suas.weather.async

import com.zendesk.suas.*

typealias Async = (dispatcher: Dispatcher, state: GetState) -> Unit

class AsyncMiddleware : Middleware {

    data class AsyncAction(val async: Async): Action<Async>("ASYNC_ACTION", async)

    companion object {
        fun create(async: Async): Action<Async> = AsyncAction(async)
    }

    override fun onAction(action: Action<*>, state: GetState, dispatcher: Dispatcher, continuation: Continuation) {
        if(action is AsyncAction && action.actionType == "ASYNC_ACTION") {
            action.async(dispatcher, state)
        } else {
            continuation.next(action)
        }
    }
}