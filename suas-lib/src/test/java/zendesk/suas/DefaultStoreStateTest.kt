package zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DefaultStoreStateTest : Helper {

    @Test
    fun `store state - test default state`() {
        val store = store()

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("empty_state")
    }

    @Test
    fun `store state - test initial state`() {
        val state = State().apply {
            updateKey(String::class.java, "custom_state")
        }

        val store = store(initialState = state)

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("custom_state")
    }


    @Test
    fun `store state - dispatch action`() {
        val store = store()

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("empty_state")

        store.dispatchAction(Action<Unit>("something"))

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("new_state")
    }

    @Test
    fun `store state - reset`() {
        val store = store()

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("empty_state")

        val state = State().apply {
            updateKey(String::class.java, "custom_state")
        }

        store.reset(state)

        assertThat(store.state.getState(String::class.java))
                .isEqualTo("custom_state")
    }


}