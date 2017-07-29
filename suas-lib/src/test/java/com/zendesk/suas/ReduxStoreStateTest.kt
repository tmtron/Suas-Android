package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import com.zendesk.suas.Helper.TestReducer
import org.junit.Test

class ReduxStoreStateTest : Helper {

    @Test
    fun `store state - test default state`() {
        val store = ReduxStore.Builder(TestReducer()).build()

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("empty_state")
    }

    @Test
    fun `store state - test initial state`() {
        val state = State().apply {
            updateKey(String::class.java, "custom_state")
        }

        val store = ReduxStore.Builder(TestReducer())
                .withInitialState(state)
                .build()

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("custom_state")
    }


    @Test
    fun `store state - dispatch action`() {
        val store = ReduxStore.Builder(TestReducer())
                .build()

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("empty_state")

        store.dispatchAction(Action<Unit>("something"))

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("new_state")
    }

    @Test
    fun `store state - reset`() {
        val store = ReduxStore.Builder(TestReducer())
                .build()

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("empty_state")

        val state = State().apply {
            updateKey(String::class.java, "custom_state")
        }

        store.resetFullState(state)

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("custom_state")
    }


}