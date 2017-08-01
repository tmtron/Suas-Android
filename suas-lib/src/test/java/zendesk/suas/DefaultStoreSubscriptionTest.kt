package zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch

class DefaultStoreSubscriptionTest : Helper {

    @Test
    fun `store subscription - trigger update`() {
        val latch = CountDownLatch(1)

        val listener = Listener { n: State ->
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.emptyState)
            latch.countDown()
        }

        val store = store()
        val subscription = store.addListener(listener)
        subscription.update()

        latch.await()
    }

    @Test
    fun `store subscription - unsubscribe`() {

        val listener = Listener<State> {
            fail("Listener must not be called")
        }

        val store = store()
        val subscription = store.addListener(listener)

        subscription.unsubscribe()

//        subscription.update() // TODO ... ?
        store.dispatchAction(Action<Unit>("test"))
    }

    @Test
    fun `store subscription - subscribe`() {

        val latch = CountDownLatch(1)

        val listener = Listener { n: State ->
            assertThat(n.getState(String::class.java)).isEqualTo(Helper.newState)
            latch.countDown("Listener must not be called")
        }

        val store = store()
        val subscription = store.addListener(listener)

        subscription.unsubscribe()
        store.dispatchAction(Action<Unit>("test"))

        subscription.subscribe()
        store.dispatchAction(Action<Unit>("test"))

        latch.awaitOrFail()
    }

}