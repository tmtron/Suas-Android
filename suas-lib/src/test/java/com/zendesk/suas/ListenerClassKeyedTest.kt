package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch

class ListenerClassKeyedTest {

    @Test
    fun `listener for class key`() {

        val latch = CountDownLatch(2)

        val oldState = State().apply {
            updateKey(String::class.java, "bla1")
        }
        val newState = State().apply {
            updateKey(String::class.java, "bla2")
        }

        val filter = Filter<String> { o, n->
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

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for class key - filter applies`() {

        val latch = CountDownLatch(1)

        val oldState = State().apply {
            updateKey(String::class.java, "bla1")
        }
        val newState = State().apply {
            updateKey(String::class.java, "bla1")
        }

        val filter = Filter<String> { o, n ->
            assertThat(o).isEqualTo("bla1")
            assertThat(n).isEqualTo("bla1")
            latch.countDown()
            false
        }

        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(oldState, newState)

        latch.await()
    }

    @Test
    fun `listener for class key - sub-state not available`() {

        val oldState = State()
        val newState = State()

        val filter = Filter<String> { _, _ ->
            fail("Filter must not be called")
            true
        }

        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(oldState, newState)
    }

    @Test
    fun `listener for class key - get key`() {
        val filter = Filter<String> { _, _ ->
            fail("Filter must not be called")
            true
        }

        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)

        assertThat(stateListener.key).isEqualTo(State.keyForClass(String::class.java))
    }

    @Test
    fun `listener for class key - equals`() {
        val filter1 = Filter<String> { _, _ -> true }
        val filter2 = Filter<String> { _, _ -> false }

        val listener = Listener<String> { _, _ -> }

        val stateListener1 = Listeners.create<String>(String::class.java, filter1, listener)
        val stateListener2 = Listeners.create<String>(String::class.java, filter2, listener)

        assertThat(stateListener1 == stateListener2).isTrue()
    }

    @Test
    fun `listener for class key - hashcode`() {
        val filter = Filter<String> { _, _ ->
            true
        }

        val listener = Listener<String> { _, _ ->
        }

        val stateListener = Listeners.create<String>(String::class.java, filter, listener)

        assertThat(stateListener.hashCode()).isEqualTo(listener.hashCode())
    }

}