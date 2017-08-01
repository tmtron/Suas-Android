package zendesk.suas

import org.junit.Test
import org.mockito.Mockito.*


class CombinedSubscriptionTest {

    @Test
    fun `combined subscription - update`() {
        val subscriptions = listOf(mock(Subscription::class.java), mock(Subscription::class.java), mock(Subscription::class.java))
        val combined = CombinedSubscription.from(subscriptions)

        combined.informWithCurrentState()

        subscriptions.forEach { verify(it, times(1)).informWithCurrentState() }
    }

    @Test
    fun `combined subscription - subscribe`() {
        val subscriptions = listOf(mock(Subscription::class.java), mock(Subscription::class.java), mock(Subscription::class.java))
        val combined = CombinedSubscription.from(subscriptions)

        combined.addListener()

        subscriptions.forEach { verify(it, times(1)).addListener() }
    }

    @Test
    fun `combined subscription - unsubscribe`() {
        val subscriptions = listOf(mock(Subscription::class.java), mock(Subscription::class.java), mock(Subscription::class.java))
        val combined = CombinedSubscription.from(subscriptions)

        combined.removeListener()

        subscriptions.forEach { verify(it, times(1)).removeListener() }
    }

}