package zendesk.suas

import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*


class AsyncMiddlewareTest {

    @Test
    fun `test async middleware - async action`() {
        val middleware = AsyncMiddleware()

        val state = GetState { State() }
        val dispatcher = Dispatcher {  }
        val continuation = mock(Continuation::class.java)
        val asyncAction = mock(AsyncAction::class.java)
        val action = AsyncMiddleware.create(asyncAction)

        middleware.onAction(action, state, dispatcher, continuation)

        verify(asyncAction).execute(dispatcher, state)
    }

    @Test
    fun `test async middleware - non async action`() {
        val middleware = AsyncMiddleware()

        val state = GetState { State() }
        val dispatcher = Dispatcher {  }
        val continuation = mock(Continuation::class.java)
        val action = Action<Unit>("bla")

        middleware.onAction(action, state, dispatcher, continuation)

        verify(continuation).next(action)
    }

}