package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch

class ListenerStateTest {

    @Test
    fun `listener for state`() {
        val latch = CountDownLatch(2)

        val oldState = State().apply {
            updateKey(String::class.java, "bla1")
        }
        val newState = State().apply {
            updateKey(String::class.java, "bla2")
        }

        val filter = Filter<State> { o, n->
            assertThat(o).isEqualTo(oldState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            true
        }

        val listener = Listener<State> { o, n ->
            assertThat(o).isEqualTo(oldState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
        }

        val stateListener = Listeners.create(filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for state - filter applies`() {
        val latch = CountDownLatch(1)

        val oldState = State().apply {
            updateKey(String::class.java, "bla1")
        }
        val newState = State().apply {
            updateKey(String::class.java, "bla2")
        }

        val filter = Filter<State> { o, n->
            assertThat(o).isEqualTo(oldState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            false
        }

        val listener = Listener<State> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for state - get key`() {
        val filter = Filter<State> { _, _ ->
            fail("Filter must not be called")
            true
        }

        val listener = Listener<State> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(filter, listener)

        assertThat(stateListener.key).isNull()
    }

    @Test
    fun `listener for state - equals`() {
        val filter1 = Filter<State> { _, _ -> true }
        val filter2 = Filter<State> { _, _ -> false }

        val listener = Listener<State> { _, _ -> }

        val stateListener1 = Listeners.create(filter1, listener)
        val stateListener2 = Listeners.create(filter2, listener)

        assertThat(stateListener1 == stateListener2).isTrue()
    }

    @Test
    fun `listener for state - hashcode`() {
        val filter = Filter<State> { _, _ ->
            true
        }

        val listener = Listener<State> { _, _ ->
        }

        val stateListener = Listeners.create(filter, listener)

        assertThat(stateListener.hashCode()).isEqualTo(listener.hashCode())
    }

}