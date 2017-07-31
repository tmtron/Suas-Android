package zendesk.suas

import org.junit.Test
import zendesk.suas.Helper.TestReducer
import java.util.concurrent.CountDownLatch

class ReduxStoreComponentTest : Helper {

    @Test
    fun `store component - register component for state`() {

        val latch = CountDownLatch(4) // 1. + 2. after connect, 3. + 4. after dispatching action
        val component = TestComponent<State, String>(latch, "data")

        val store = store()
        store.connect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for state`() {

        val latch = CountDownLatch(2) // 1. + 2. after connect
        val component = TestComponent<State, String>(latch, "data")

        val store = store()
        store.connect(component)
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for state with filter`() {

        val latch = CountDownLatch(6) // 1. + 2. + 3. after connect, 4. + 5. + 6. after dispatching action
        val component = TestComponent<State, String>(latch, "data")

        val filter = Filter { _: State, _: State ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store()
        store.connect(component, filter)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for state with filter`() {

        val latch = CountDownLatch(3) // 1. + 2. + 3. after connect
        val component = TestComponent<State, String>(latch, "data")

        val filter = Filter { _: State, _: State ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store()
        store.connect(component, filter)
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for string`() {

        val latch = CountDownLatch(4)
        val component = TestComponent<String, String>(latch, "data")

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key")
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for string`() {

        val latch = CountDownLatch(2)
        val component = TestComponent<String, String>(latch, "data")

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key")
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for string with filter`() {

        val latch = CountDownLatch(6) // 1. + 2. + 3. after connect, 4. + 5. + 6. after dispatching action
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", filter)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for string with filter`() {

        val latch = CountDownLatch(3) // 1. + 2. + 3. after connect
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", filter)
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for string with filter, filter filters`() {

        val latch = CountDownLatch(2) // 1. + 2. + 3. after connect, 4. + 5. + 6. after dispatching action
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            false
        }

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", filter)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for class`() {

        val latch = CountDownLatch(4)
        val component = TestComponent<String, String>(latch, "data")

        val store = store()
        store.connect(component, String::class.java)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for class`() {

        val latch = CountDownLatch(2)
        val component = TestComponent<String, String>(latch, "data")

        val store = store()
        store.connect(component, String::class.java)
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for class with filter`() {

        val latch = CountDownLatch(6) // 1. + 2. + 3. after connect, 4. + 5. + 6. after dispatching action
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store()
        store.connect(component, String::class.java, filter)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for class with filter`() {

        val latch = CountDownLatch(3) // 1. + 2. + 3. after connect
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store()
        store.connect(component, String::class.java, filter)
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for class with filter, filter filters`() {

        val latch = CountDownLatch(2) // 1. + 2. + 3. after connect, 4. + 5. + 6. after dispatching action
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            false
        }

        val store = store()
        store.connect(component, String::class.java, filter)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }


    @Test
    fun `store component - register component for class and string`() {

        val latch = CountDownLatch(4)
        val component = TestComponent<String, String>(latch, "data")

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", String::class.java)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for class and string`() {

        val latch = CountDownLatch(2)
        val component = TestComponent<String, String>(latch, "data")

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", String::class.java)
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for class and string with filter`() {

        val latch = CountDownLatch(6) // 1. + 2. + 3. after connect, 4. + 5. + 6. after dispatching action
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", String::class.java, filter)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - unregister component for class and string with filter`() {

        val latch = CountDownLatch(3) // 1. + 2. + 3. after connect
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            true
        }

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", String::class.java, filter)
        store.disconnect(component)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `store component - register component for class and string with filter, filter filters`() {

        val latch = CountDownLatch(2) // 1. + 2. + 3. after connect, 4. + 5. + 6. after dispatching action
        val component = TestComponent<String, String>(latch, "data")

        val filter = Filter { _: String, _: String ->
            latch.countDown("Filter must not be called")
            false
        }

        val store = store(reducer = TestReducer("key"))
        store.connect(component, "key", String::class.java, filter)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    private class TestComponent<E, F>(val latch: CountDownLatch? = null, val select: F?) : Component<E, F>, Helper{
        override fun update(e: F) {
            latch?.countDown("Update must not be called")
        }

        override fun getSelector(): StateSelector<E, F> = StateSelector { _ ->
            latch?.countDown("Selector must not be called")
            select
        }
    }

}

