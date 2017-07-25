package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.mockito.Mockito.*

class CombinedMiddlewareTest {

    private val getState = GetState { State() }
    private val dispatcher = Dispatcher {  }

    @Test
    fun `no middleware provided`() {
        val cm = CombinedMiddleware(null)
        val action = Action<Unit>("wurst")

        cm.onAction(action, { State() }, {}) {
            assertThat(it).isEqualTo(action)
        }
    }

    @Test
    fun `with middleware`() {
        val m1 = TestMiddleware(eatAction = false)
        val m2 = TestMiddleware(eatAction = false)
        val m3 = TestMiddleware(eatAction = false)

        val cm = CombinedMiddleware(listOf(m1, m2, m3))

        val continuation: Continuation = mock(Continuation::class.java)
        val action = Action<Unit>("wurst")
        cm.onAction(action, getState, dispatcher, continuation)

        verify(continuation, times(1)).next(eq(action))
        assertThat(m1.wasCalled).isTrue()
        assertThat(m2.wasCalled).isTrue()
        assertThat(m3.wasCalled).isTrue()
    }

    @Test
    fun `middleware provided - 2nd eats action`() {
        val m1 = TestMiddleware(eatAction = false)
        val m2 = TestMiddleware(eatAction = true)
        val m3 = TestMiddleware(eatAction = false)

        val cm = CombinedMiddleware(listOf(m1, m2, m3))

        val action = Action<Unit>("wurst")
        val continuation: Continuation = mock(Continuation::class.java)

        cm.onAction(action, getState, dispatcher, continuation)

        assertThat(m1.wasCalled).isTrue()
        assertThat(m2.wasCalled).isTrue()
        assertThat(m3.wasCalled).isFalse()
        verifyZeroInteractions(continuation)
    }


    open class TestMiddleware(val eatAction: Boolean = false) : Middleware {

        var wasCalled = false

        override fun onAction(action: Action<*>, state: GetState, dispatcher: Dispatcher, continuation: Continuation) {
            wasCalled = true
            if (!eatAction) {
                continuation.next(action)
            }
        }
    }
}