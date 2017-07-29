package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReducerTest {

    @Test
    fun `reducer test default key`() {
        val reducer = object : Reducer<String>() {
            override fun reduce(oldState: String, action: Action<*>): String? {
                return "new_string"
            }

            override fun getEmptyState(): String = "empty"
        }

        assertThat(reducer.key).isEqualTo(State.keyForClass(String::class.java))
    }

}