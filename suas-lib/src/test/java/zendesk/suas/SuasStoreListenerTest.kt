package zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import zendesk.suas.Helper.TestReducer
import java.util.concurrent.CountDownLatch

class SuasStoreListenerTest : Helper {

    @Test
    fun `store listener - register state listener`() {
        val latch = CountDownLatch(1)

        val listener = Listener { n: State ->
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val store = store()
        store.addListener(listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister state listener`() {
        val listener = Listener<State> {
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

        val listener = Listener { n: State ->
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val filter = Filter { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(Helper.emptyState)
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.newState)
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

        val listener = Listener<State> {
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

        val listener = Listener<State> {
            fail("Listener must not be called")
        }

        val filter = Filter { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(Helper.emptyState)
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.newState)
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

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister string key listener`() {
        val listener = Listener<String> {
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

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(Helper.emptyState)
            assertThat(n).isEqualTo(Helper.newState)
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

        val listener = Listener<String> {
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

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(Helper.emptyState)
            assertThat(n).isEqualTo(Helper.newState)
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

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val store = store()
        store.addListener(String::class.java, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister class key listener`() {
        val listener = Listener<String> {
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

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(Helper.emptyState)
            assertThat(n).isEqualTo(Helper.newState)
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

        val listener = Listener<String> {
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

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(Helper.emptyState)
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
            false
        }

        val store = store()
        store.addListener(String::class.java, filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }


    @Test
    fun `store listener - register class and string key listener`() {
        val latch = CountDownLatch(1)

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", String::class.java, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister class and string listener`() {
        val listener = Listener<String> {
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

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(Helper.emptyState)
            assertThat(n).isEqualTo(Helper.newState)
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

        val listener = Listener<String> {
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

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val filter = Filter { o: String, n: String ->
            assertThat(o).isEqualTo(Helper.emptyState)
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
            false
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener("key", String::class.java, filter, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    // HERE



    @Test
    fun `store listener - register state listener with selector`() {
        val latch = CountDownLatch(2)

        val selector = StateSelector {
            latch.countDown()
            it.getState(String::class.java)
        }

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val store = store()
        store.addListener(selector, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.awaitOrFail()
    }

    @Test
    fun `store listener - unregister state listener with selector`() {
        val selector = StateSelector {
            fail("Listener must not be called")
            ""
        }

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val store = store()
        store.addListener(selector, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }


    @Test
    fun `store listener - register state listener with selector with filter`() {
        val latch = CountDownLatch(3)

        val selector = StateSelector {
            latch.countDown()
            it.getState(String::class.java)
        }

        val listener = Listener { n: String ->
            assertThat(n).isEqualTo(Helper.newState)
            latch.countDown()
        }

        val filter = Filter { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(Helper.emptyState)
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.newState)
            latch.countDown()
            true
        }

        val store = store()
        store.addListener(filter, selector, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }

    @Test
    fun `store listener - unregister state listener with selector with filter`() {

        val selector = StateSelector {
            fail("Selector must not be called")
            ""
        }

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val filter = Filter<State> { _, _ ->
            fail("Listener must not be called")
            false
        }

        val store = store(reducer = TestReducer("key"))
        store.addListener(filter, selector, listener)
        store.removeListener(listener)

        store.dispatchAction(Action<Unit>("test"))
    }

    @Test
    fun `store listener - register state listener with selector with filter, filter filters`() {
        val latch = CountDownLatch(1)

        val selector = StateSelector {
            fail("Selector must not be called")
            ""
        }

        val listener = Listener<String> {
            fail("Listener must not be called")
        }

        val filter = Filter { o: State, n: State ->
            assertThat(o.getState(String::class.java)).isEqualTo(Helper.emptyState)
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.newState)
            latch.countDown()
            false
        }

        val store = store()
        store.addListener(filter, selector, listener)
        store.dispatchAction(Action<Unit>("test"))

        latch.await()
    }
}