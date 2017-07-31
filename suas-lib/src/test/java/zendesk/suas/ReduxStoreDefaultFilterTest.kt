package zendesk.suas

import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch

class ReduxStoreDefaultFilterTest : Helper {

    @Test
    fun `redux store - custom default filter - filter filters`() {

        val latch = CountDownLatch(1)

        val customDefaultFilter = Filter<Any> { _, _ ->
            latch.countDown("Filter must not be called")
            false
        }

        val listener = Listener { `<anonymous parameter 1>`: State ->
            fail("Listener must not be called")
        }

        val store = store(filter = customDefaultFilter)
        store.addListener(listener)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `redux store - custom default filter - filter does not filter`() {

        val latch = CountDownLatch(2)

        val customDefaultFilter = Filter<Any> { _, _ ->
            latch.countDown("Filter must not be called")
            true
        }

        val listener = Listener { `<anonymous parameter 1>`: State ->
            latch.countDown("Listener must not be called")
        }

        val store = store(filter = customDefaultFilter)
        store.addListener(listener)
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

}