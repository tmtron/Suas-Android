package zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch

class ListenerStateSelector : Helper {

    @Test
    fun `listener for state with selector`() {

        val latch = CountDownLatch(2)

        val oldState = State(mutableMapOf<String, Any>("key" to "bla1"))
        val newState = State(mutableMapOf<String, Any>("key" to "bla2"))

        val selector = StateSelector {
            it.getState("key", String::class.java)
        }

        val filter = Filter<State> { o, n ->
            assertThat(o).isEqualTo(oldState)
            assertThat(n).isEqualTo(newState)
            latch.countDown("Filter must not be called")
            true
        }

        val listener = Listener<String> { n ->
            assertThat(n).isEqualTo("bla2")
            latch.countDown("Listener must not be called")
        }

        val stateListener = Listeners.create(selector, filter, listener)
        stateListener.update(oldState, newState, false)

        latch.await()
    }

    @Test
    fun `listener for state with selector - filter applies`() {

        val latch = CountDownLatch(1)

        val oldState = State(mutableMapOf<String, Any>("key" to "bla1"))
        val newState = State(mutableMapOf<String, Any>("key" to "bla2"))

        val selector = StateSelector {
            it.getState("key", String::class.java)
        }

        val filter = Filter<State> { o, n ->
            assertThat(o).isEqualTo(oldState)
            assertThat(n).isEqualTo(newState)
            latch.countDown("Filter must not be called")
            false
        }

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(selector, filter, listener)
        stateListener.update(oldState, newState, false)

        latch.awaitOrFail()
    }



    @Test
    fun `listener for string key - get key`() {
        val stateListener = Listeners.create({}, { _,_ -> true }, {})

        assertThat(stateListener.stateKey).isNull()
    }

    @Test
    fun `listener for string key - skip filter`() {
        val latch = CountDownLatch(1)

        val oldState = State(mutableMapOf<String, Any>("key" to "bla1"))
        val newState = State(mutableMapOf<String, Any>("key" to "bla2"))

        val selector = StateSelector {
            it.getState("key", String::class.java)
        }

        val filter = Filter<State> { _, _ ->
            fail("Filter must not be called")
            false
        }

        val listener = Listener<String> {
            assertThat(it).isEqualTo("bla2")
            latch.countDown("Listener must not be called")
        }

        val stateListener = Listeners.create(selector, filter, listener)
        stateListener.update(oldState, newState, true)

        latch.awaitOrFail()
    }

    @Test
    fun `listener for class key - null state`() {
        val selector = StateSelector {
            it.getState("key", String::class.java)
        }

        val filter = Filter<State> { _, _ ->
            fail("Filter must not be called")
            false
        }

        val listener = Listener<String> { _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(selector, filter, listener)
        stateListener.update(State(), null, false)
        stateListener.update(null, State(), false)
        stateListener.update(null, null, false)
    }

}