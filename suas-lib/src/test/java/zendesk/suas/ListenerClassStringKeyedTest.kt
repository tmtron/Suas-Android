package zendesk.suas

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

        val listener = Listener<Date> { n ->
            assertThat(n).isEqualTo(Date(2))
            latch.countDown()
        }

        val stateListener = Listeners.create("key", Date::class.java, filter, listener)
        stateListener.update(oldState, newState, false)

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

        val listener = Listener<Date> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("key", Date::class.java, filter, listener)
        stateListener.update(oldState, newState, false)

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

        val listener = Listener<Date> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("wrong_key", Date::class.java, filter, listener)
        stateListener.update(newState, oldState, false)
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

        val listener = Listener<Date> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("wrong_key", Date::class.java, filter, listener)
        stateListener.update(newState, oldState, false)
    }

    @Test
    fun `listener for class and string key - get key`() {
        val filter = Filter<Date> { _, _ ->
            fail("Listener must not be called")
            true
        }

        val listener = Listener<Date> {
            fail("Listener must not be called")
        }

        val stateListener = Listeners.create("key", Date::class.java, filter, listener)

        assertThat(stateListener.stateKey).isEqualTo("key")
    }

}