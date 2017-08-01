package zendesk.suas

import org.junit.Assert.fail
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

interface Helper {

    companion object {
        const val emptyState = "empty_state"
        const val newState = "new_state"
    }

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
            withExecutor(Executors.getDefaultExecutor())
            if(filter != null) withDefaultFilter(filter)
            if(initialState != null) withInitialState(initialState)
        }.build()
    }

    class TestReducer(val customKey: String? = null) : Reducer<String>() {
        override fun getInitialState(): String {
            return emptyState
        }

        override fun reduce(oldState: String, action: Action<*>): String? {
            return newState
        }

        override fun getStateKey(): String {
            return customKey ?: super.getStateKey()
        }
    }

}