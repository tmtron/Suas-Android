package zendesk.suas

import org.junit.Assert.fail
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

interface Helper {
    fun CountDownLatch.countDown(message: String) {
        if (count == 0L) fail(message)
        else countDown()
    }

    fun CountDownLatch.awaitOrFail() {
        val result = await(1L, TimeUnit.SECONDS)
        if(!result) fail("Timeout")
    }

    fun store(reducer: Reducer<*> = TestReducer(),
              filter: Filter<Any>? = null,
              middleware: List<Middleware> = listOf(),
              initialState: State? = null
              ): Store {
        return Suas.createStore(reducer).apply {
            withMiddleware(middleware)
            if(filter != null) withDefaultFilter(filter)
            if(initialState != null) withInitialState(initialState)
        }.build()
    }

    class TestReducer(val customKey: String? = null) : Reducer<String>() {
        override fun getInitialState(): String {
            return ReduxStoreListenerTest.initialState
        }

        override fun reduce(oldState: String, action: Action<*>): String? {
            return ReduxStoreListenerTest.newState
        }

        override fun getStateKey(): String {
            return customKey ?: super.getStateKey()
        }
    }

}