package zendesk.suas

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

        val listener = Listener<String> { n ->
            assertThat(n).isEqualTo("bla2")
            latch.countDown()
        }

        val stateListener = Listeners.create("key", filter, listener)
        stateListener.update(oldState, newState, false)

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

        val listener = Listener<String> {
            Assert.fail("Listener must not be called")
        }

        val stateListener = Listeners.create("key", filter, listener)
        stateListener.update(newState, oldState, false)

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

        val listener = Listener<String> {
            Assert.fail("Listener must not be called")
        }

        val stateListener: Listeners.StateListener = Listeners.create<String>("key", filter, listener)
        stateListener.update(newState, oldState, false)
    }

    @Test
    fun `listener for string key - null type`() {

        val oldState = State(mutableMapOf<String, Any>("key" to Date(1)))
        val newState = State(mutableMapOf<String, Any>("key" to Date(2)))

        val filter = Filter<String> { _, _ ->
            Assert.fail("Filter must not be called")
            true
        }

        val listener = Listener<String> {
            Assert.fail("Listener must not be called")
        }

        val stateListener = Listeners.create<String>("key1", filter, listener)
        stateListener.update(newState, oldState, false)
    }


    @Test
    fun `listener for string key - get key`() {
        val stateListener = Listeners.create<String>("key1", { _, _ -> true }, { })

        assertThat(stateListener.stateKey).isEqualTo("key1")
    }

}