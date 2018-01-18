package zendesk.suas

import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch

class SuasStoreActionListenerTest : Helper {

    @Test
    fun `action listener - notify on action`() {

        val latch = CountDownLatch(1)
        val store = store()
        val action = Action<Void>("key")

        val listener = Listener<Action<*>> {
            assertThat(it.actionType).isEqualTo("key")
            latch.countDown()
        }

        store.addActionListener(listener)
        store.dispatch(action)

        latch.awaitOrFail()
    }

    @Test
    fun `action listener - unsubscribed listener receives no action`() {
        val store = store()
        val action = Action<Void>("key")

        val listener = Listener<Action<*>> {
            fail("Listener should not be called")
        }

        store.addActionListener(listener)
        store.removeListener(listener)
        store.dispatch(action)
    }

    @Test
    fun `action listener - unsubscribed listener receives no action, through subscription`() {
        val store = store()
        val action = Action<Void>("key")

        val listener = Listener<Action<*>> {
            fail("Listener should not be called")
        }

        val subscription = store.addActionListener(listener)
        subscription.removeListener()

        store.dispatch(action)
    }

    @Test
    fun `action listener - re-added listener receives action`() {

        val latch = CountDownLatch(1)
        val store = store()
        val action = Action<Void>("key")

        val listener = Listener<Action<*>> {
            assertThat(it.actionType).isEqualTo("key")
            latch.countDown()
        }

        val subscription = store.addActionListener(listener)
        subscription.removeListener()
        subscription.addListener()

        store.dispatch(action)

        latch.awaitOrFail()
    }

}