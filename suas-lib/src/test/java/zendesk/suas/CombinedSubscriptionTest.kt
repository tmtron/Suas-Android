package zendesk.suas

import org.junit.Test
import org.mockito.Mockito.*


class CombinedSubscriptionTest {

    @Test
    fun `combined subscription - update`() {
        val subscriptions = listOf(mock(Subscription::class.java), mock(Subscription::class.java), mock(Subscription::class.java))
        val combined = CombinedSubscription.from(subscriptions)

        combined.update()

        subscriptions.forEach { verify(it, times(1)).update() }
    }

    @Test
    fun `combined subscription - subscribe`() {
        val subscriptions = listOf(mock(Subscription::class.java), mock(Subscription::class.java), mock(Subscription::class.java))
        val combined = CombinedSubscription.from(subscriptions)

        combined.subscribe()

        subscriptions.forEach { verify(it, times(1)).subscribe() }
    }

    @Test
    fun `combined subscription - unsubscribe`() {
        val subscriptions = listOf(mock(Subscription::class.java), mock(Subscription::class.java), mock(Subscription::class.java))
        val combined = CombinedSubscription.from(subscriptions)

        combined.unsubscribe()

        subscriptions.forEach { verify(it, times(1)).unsubscribe() }
    }

}