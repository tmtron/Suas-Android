package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch

class ListenerStringKeyedTest {

    @Test
    fun `listener for string key`() {

        val latch = CountDownLatch(2)

        val oldState = State(mutableMapOf<String, Any>("key" to "bla1"))
        val newState = State(mutableMapOf<String, Any>("key" to "bla2"))

        val filter = Filter<String> { o, n ->
            assertThat(o).isEqualTo("bla1")
            assertThat(n).isEqualTo("bla2")
            latch.countDown()
            true
        }

        val listener = Listener<String> { o, n ->
            assertThat(o).isEqualTo("bla1")
            assertThat(n).isEqualTo("bla2")
            latch.countDown()
        }

        val stateListener = Listeners.create("key", filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for string key - filter applies`() {

        val latch = CountDownLatch(1)

        val oldState = State(mutableMapOf<String, Any>("key" to "bla1"))
        val newState = State(mutableMapOf<String, Any>("key" to "bla1"))

        val filter = Filter<String> { o, n ->
            assertThat(o).isEqualTo("bla1")
            assertThat(n).isEqualTo("bla1")
            latch.countDown()
            false
        }

        val listener = Listener<String> { _, _ ->
            Assert.fail("Listener must not be called")
        }

        val stateListener = Listeners.create("key", filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for string key - wrong type`() {

        val oldState = State(mutableMapOf<String, Any>("key" to Date(1)))
        val newState = State(mutableMapOf<String, Any>("key" to Date(2)))

        val filter = Filter<String> { _, _ ->
            Assert.fail("Filter must not be called")
            true
        }

        val listener = Listener<String> { _, _ ->
            Assert.fail("Listener must not be called")
        }

        val stateListener: Listeners.StateListener = Listeners.create<String>("key", filter, listener)
        stateListener.update(oldState, newState)
    }

    @Test
    fun `listener for string key - null type`() {

        val oldState = State(mutableMapOf<String, Any>("key" to Date(1)))
        val newState = State(mutableMapOf<String, Any>("key" to Date(2)))

        val filter = Filter<String> { _, _ ->
            Assert.fail("Filter must not be called")
            true
        }

        val listener = Listener<String> { _, _ ->
            Assert.fail("Listener must not be called")
        }

        val stateListener = Listeners.create<String>("key1", filter, listener)
        stateListener.update(oldState, newState)
    }


    @Test
    fun `listener for string key - get key`() {
        val stateListener = Listeners.create<String>("key1", { _, _ -> true }, { _, _ -> })

        assertThat(stateListener.key).isEqualTo("key1")
    }

    @Test
    fun `listener for string key - equals`() {
        val filter1 = Filter<State> { _, _ -> true }
        val filter2 = Filter<State> { _, _ -> false }

        val listener = Listener<State> { _, _ -> }

        val stateListener1 = Listeners.create("key1", filter1, listener)
        val stateListener2 = Listeners.create("key2", filter2, listener)

        assertThat(stateListener1 == stateListener2).isTrue()
    }

    @Test
    fun `listener for string key - hashcode`() {

        val filter = Filter<String> { _, _ ->
            true
        }

        val listener = Listener<String> { _, _ ->
        }

        val stateListener = Listeners.create("key1", filter, listener)

        assertThat(stateListener.hashCode()).isEqualTo(listener.hashCode())
    }

}