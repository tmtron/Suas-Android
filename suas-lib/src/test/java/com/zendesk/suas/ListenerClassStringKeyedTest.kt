package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CountDownLatch

class ListenerClassStringKeyedTest {

    @Test
    fun `listener for class and string key`() {

        val latch = CountDownLatch(2)

        val oldState = State().apply {
            updateKey("key", Date(1))
        }
        val newState = State().apply {
            updateKey("key", Date(2))
        }

        val filter = Filter<Date> { o, n ->
            assertThat(o).isEqualTo(Date(1))
            assertThat(n).isEqualTo(Date(2))
            latch.countDown()
            true
        }

        val listener = Listener<Date> { o, n ->
            assertThat(o).isEqualTo(Date(1))
            assertThat(n).isEqualTo(Date(2))
            latch.countDown()
        }

        val stateListener = Listeners.create("key", Date::class.java, filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for class and string key - filter applies`() {
        val latch = CountDownLatch(1)

        val oldState = State().apply {
            updateKey("key", Date(1))
        }
        val newState = State().apply {
            updateKey("key", Date(2))
        }

        val filter = Filter<Date> { o, n ->
            assertThat(o).isEqualTo(Date(1))
            assertThat(n).isEqualTo(Date(2))
            latch.countDown()
            false
        }

        val listener = Listener<Date> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("key", Date::class.java, filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for class and string key - key not available`() {
        val oldState = State().apply {
            updateKey("key", Date(1))
        }
        val newState = State().apply {
            updateKey("key", Date(2))
        }

        val filter = Filter<Date> { _, _ ->
            fail("Listener must not be called")
            true
        }

        val listener = Listener<Date> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("wrong_key", Date::class.java, filter, listener)
        stateListener.update(oldState, newState)
    }

    @Test
    fun `listener for class and string key - wrong type`() {
        val oldState = State().apply {
            updateKey("key", BigDecimal(1))
        }
        val newState = State().apply {
            updateKey("key", BigDecimal(2))
        }

        val filter = Filter<Date> { _, _ ->
            fail("Listener must not be called")
            true
        }

        val listener = Listener<Date> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("wrong_key", Date::class.java, filter, listener)
        stateListener.update(oldState, newState)
    }

    @Test
    fun `listener for class and string key - get key`() {
        val filter = Filter<Date> { _, _ ->
            fail("Listener must not be called")
            true
        }

        val listener = Listener<Date> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("key", Date::class.java, filter, listener)

        assertThat(stateListener.key).isEqualTo("key")
    }

    @Test
    fun `listener for class and string key - equals`() {
        val filter1 = Filter<String> { _, _ -> true }
        val filter2 = Filter<String> { _, _ -> false }

        val listener = Listener<String> { _, _ -> }

        val stateListener1 = Listeners.create("key1", String::class.java, filter1, listener)
        val stateListener2 = Listeners.create("key2", String::class.java, filter2, listener)

        assertThat(stateListener1 == stateListener2).isTrue()
    }

    @Test
    fun `listener for class and string key - hashcode`() {
        val filter = Filter<Date> { _, _ ->
            true
        }

        val listener = Listener<Date> { _, _ ->

        }

        val stateListener = Listeners.create("key", Date::class.java, filter, listener)

        assertThat(stateListener.hashCode()).isEqualTo(listener.hashCode())
    }

}