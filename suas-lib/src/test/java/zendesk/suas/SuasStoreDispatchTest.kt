package zendesk.suas

import org.junit.Test

class SuasStoreDispatchTest : Helper {

    @Test(expected = RuntimeException::class)
    fun `dispatch from reducer should crash`() {
        val store = store(reducer = BadReducer())
        store.dispatch(DispatcherAction(store))
    }

    class DispatcherAction(val dispatcher: Dispatcher) : Action<Dispatcher>(":/", dispatcher)

    class BadReducer : Reducer<String>() {
        override fun reduce(oldState: String, action: Action<*>): String? {
            return if(action is DispatcherAction) {
                action.dispatcher.dispatch(Action<String>("DONT DO THIS EVER"))
                null
            } else {
                ""
            }
        }

        override fun getInitialState(): String = ""
    }

}