package zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import zendesk.suas.Helper.TestReducer
import java.util.concurrent.CountDownLatch

class ReduxStoreListenerTest : Helper {

    companion object {
        const val initialState = "empty_state"
        const val newState = "new_state"
    }

    @Test
    fun `store listener - register state listener`() {
        val latch = CountDownLatch(1)

        val listener = Listener { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(initialState)
            assertThat(n.getState(String::class.java)).isEqualTo(newState)
            latch.countDown()
        }

        val store = store()
        store.addListener(listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister state listener`() {
        val listener = Listener<State> { _, _ ->
            fail("Listener must not be called")
        }

        val store = store()

        store.addListener(listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }



    @Test
    fun `store listener - register state listener with filter`() {
        val latch = CountDownLatch(2)

        val listener = Listener { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(initialState)
            assertThat(n.getState(String::class.java)).isEqualTo(newState)
            latch.countDown()
        }

        val filter = Filter { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(initialState)
            assertThat(n.getState(String::class.java)).isEqualTo(newState)
            latch.countDown()
            true
        }

        val store = store()
        store.addListener(filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister state listener with filter`() {

        val listener = Listener<State> { _, _ ->
            fail("Listener must not be called")
        }

        val filter = Filter<State> { _, _ ->
            fail("Listener must not be called")
            false
        }

        val store = store()
        store.addListener(filter, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }

    @Test
    fun `store listener - register state listener with filter, filter filters`() {
        val latch = CountDownLatch(1)

        val listener = Listener { _: State, _: State ->
            fail("Listener must not be called")
        }

        val filter = Filter { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(initialState)
            assertThat(n.getState(String::class.java)).isEqualTo(newState)
            latch.countDown()
            false
        }

        val store = store()
        store.addListener(filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }



    @Test
    fun `store listener - register string key listener`() {
        val latch = CountDownLatch(1)

        val listener = Listener { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister string key listener`() {
        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val store = store(reducer = TestReducer("key"))

        store.addListener("key", listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }


    @Test
    fun `store listener - register string key listener with filter`() {
        val latch = CountDownLatch(2)

        val listener = Listener { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            true
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister string key listener with filter`() {

        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val filter = Filter<String> { _, _ ->
            fail("Listener must not be called")
            false
        }

        val store = store(reducer = TestReducer("key"))

        store.addListener("key", filter, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }

    @Test
    fun `store listener - register string key listener with filter, filter filters`() {
        val latch = CountDownLatch(1)

        val listener = Listener { _: String, _: String ->
            fail("Listener must not be called")
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            false
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }


    @Test
    fun `store listener - register class key listener`() {
        val latch = CountDownLatch(1)

        val listener = Listener { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
        }

        val store = store()
        store.addListener(String::class.java, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister class key listener`() {
        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val store = store()

        store.addListener(String::class.java, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }


    @Test
    fun `store listener - register class key listener with filter`() {
        val latch = CountDownLatch(2)

        val listener = Listener { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            true
        }

        val store = store()
        store.addListener(String::class.java, filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister class key listener with filter`() {

        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val filter = Filter<String> { _, _ ->
            fail("Listener must not be called")
            false
        }

        val store = store()

        store.addListener(String::class.java, filter, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }

    @Test
    fun `store listener - register class key listener with filter, filter filters`() {
        val latch = CountDownLatch(1)

        val listener = Listener { _: String, _: String ->
            fail("Listener must not be called")
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            false
        }

        val store = store()
        store.addListener(String::class.java, filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }


    // HERE


    @Test
    fun `store listener - register class and string key listener`() {
        val latch = CountDownLatch(1)

        val listener = Listener { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", String::class.java, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister class and string listener`() {
        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", String::class.java, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }


    @Test
    fun `store listener - register class and string listener with filter`() {
        val latch = CountDownLatch(2)

        val listener = Listener { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            true
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", String::class.java, filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister class and string listener with filter`() {

        val listener = Listener<String> { _, _ ->
            fail("Listener must not be called")
        }

        val filter = Filter<String> { _, _ ->
            fail("Listener must not be called")
            false
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", String::class.java, filter, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }

    @Test
    fun `store listener - register class and string listener with filter, filter filters`() {
        val latch = CountDownLatch(1)

        val listener = Listener { _: String, _: String ->
            fail("Listener must not be called")
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(initialState)
            assertThat(n).isEqualTo(newState)
            latch.countDown()
            false
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", String::class.java, filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }
}