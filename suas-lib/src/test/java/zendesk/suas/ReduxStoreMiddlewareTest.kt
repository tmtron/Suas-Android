package zendesk.suas

import org.junit.Test
import java.util.concurrent.CountDownLatch

class ReduxStoreMiddlewareTest : Helper {

    @Test
    fun `redux store - middleware`() {
        val latch = CountDownLatch(1)
        val store = store(middleware = listOf(TestMiddleware(latch, false)))
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `redux store - more than one middleware`() {
        val latch = CountDownLatch(2)
        val store = store(middleware = listOf(TestMiddleware(latch, false), TestMiddleware(latch, false)))
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }

    @Test
    fun `redux store - more than one middleware, first one eats action`() {
        val latch = CountDownLatch(1)
        val store = store(middleware = listOf(TestMiddleware(latch, true), TestMiddleware(latch, false)))
        store.dispatchAction(Action<Unit>("bla"))

        latch.awaitOrFail()
    }


    class TestMiddleware(val latch: CountDownLatch, val eatAction: Boolean = false): Middleware, Helper {

        override fun onAction(action: Action<*>, state: GetState, dispatcher: Dispatcher, continuation: Continuation) {
            latch.countDown("Middleware must not be called")
            if(!eatAction) continuation.next(action)
        }
    }

}