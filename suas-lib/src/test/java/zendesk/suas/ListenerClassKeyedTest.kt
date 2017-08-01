package zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch

class ListenerClassKeyedTest : Helper {

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
            latch.countDown("Filter must not be called")
            true
        }

        val listener = Listener<String> { n ->
            assertThat(n).isEqualTo("bla2")
            latch.countDown("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(oldState, newState, false)

        latch.awaitOrFail()
    }

    @Test
    fun `listener for class key - filter applies`() {

        val latch = CountDownLatch(1)

        val oldState = State().apply {
            updateKey(String::class.java, "bla1")
        }
        val newState = State().apply {
            updateKey(String::class.java, "bla2")
        }

        val filter = Filter<String> { o, n ->
            assertThat(o).isEqualTo("bla1")
            assertThat(n).isEqualTo("bla2")
            latch.countDown("Filter must not be called")
            false
        }

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(oldState, newState, false)

        latch.awaitOrFail()
    }

    @Test
    fun `listener for class key - sub-state not available`() {

        val oldState = State()
        val newState = State()

        val filter = Filter<String> { _, _ ->
            fail("Filter must not be called")
            true
        }

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(newState, oldState, false)
    }

    @Test
    fun `listener for class key - get key`() {
        val filter = Filter<String> { _, _ ->
            fail("Filter must not be called")
            true
        }

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)

        assertThat(stateListener.stateKey).isEqualTo(State.keyForClass(String::class.java))
    }

    @Test
    fun `listener for class key - skip filter`() {

        val latch = CountDownLatch(1)

        val oldState = State().apply {
            updateKey(String::class.java, "bla1")
        }
        val newState = State().apply {
            updateKey(String::class.java, "bla2")
        }

        val filter = Filter<String> { _, _ ->
            fail("Filter must not be called")
            false
        }

        val listener = Listener<String> { n ->
            assertThat(n).isEqualTo("bla2")
            latch.countDown()
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(oldState, newState, true)

        latch.awaitOrFail()
    }

    @Test
    fun `listener for class key - null state`() {
        val filter = Filter<String> { _, _ ->
            fail("Filter must not be called")
            false
        }

        val listener = Listener<String> { _ ->
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create(String::class.java, filter, listener)
        stateListener.update(State(), null, false)
        stateListener.update(null, State(), false)
        stateListener.update(null, null, false)
    }

}