package zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CombinedReducerTest {

    @Test
    fun `create`() {
        CombinedReducer(listOf(TestReducer("1"), TestReducer("2"), TestReducer("3")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create non unique keys`() {
        CombinedReducer(listOf(TestReducer("1"), TestReducer("1"), TestReducer("3")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create no reducers`() {
        CombinedReducer(listOf())
    }

    @Test
    fun `empty state`() {
        val r1 = TestReducer("1")
        val r2 = TestReducer("2")
        val r3 = TestReducer("3")
        val cr = CombinedReducer(listOf(r1, r2, r3))

        val state = cr.emptyState

        assertThat(state.getState("1")).isEqualTo(r1.initialState)
        assertThat(state.getState("2")).isEqualTo(r2.initialState)
        assertThat(state.getState("3")).isEqualTo(r3.initialState)
    }

    @Test
    fun `reduce`() {
        val r1 = TestReducer("1", "new_state_1")
        val r2 = TestReducer("2", "new_state_2")
        val r3 = TestReducer("3", "new_state_3")
        val cr = CombinedReducer(listOf(r1, r2, r3))

        val newState = cr.reduce(cr.emptyState, Action<Unit>("bla"))

        assertThat(newState.newState.getState("1")).isEqualTo(r1.newState)
        assertThat(newState.newState.getState("2")).isEqualTo(r2.newState)
        assertThat(newState.newState.getState("3")).isEqualTo(r3.newState)
        assertThat(newState.updatedKeys).containsAllOf("1", "2", "3")
    }

    @Test
    fun `reduce - no changes null`() {
        val r1 = TestReducer("1", null)
        val r2 = TestReducer("2", "new_state_2")
        val r3 = TestReducer("3", null)
        val cr = CombinedReducer(listOf(r1, r2, r3))

        val newState = cr.reduce(cr.emptyState, Action<Unit>("bla"))

        assertThat(newState.newState.getState("1")).isEqualTo(r1.initialState)
        assertThat(newState.newState.getState("2")).isEqualTo(r2.newState)
        assertThat(newState.newState.getState("3")).isEqualTo(r3.initialState)
        assertThat(newState.updatedKeys).hasSize(1)
        assertThat(newState.updatedKeys).contains("2")
    }

    @Test
    fun `reduce - no changes same instance`() {
        val initState1 = "init_state_1"
        val initState3 = "init_state_3"
        val r1 = TestReducerOldState("1", initState1)
        val r2 = TestReducer("2", "new_state_2")
        val r3 = TestReducerOldState("3", initState3)
        val cr = CombinedReducer(listOf(r1, r2, r3))

        val newState = cr.reduce(cr.emptyState, Action<Unit>("bla"))

        assertThat(newState.newState.getState("1")).isSameAs(initState1)
        assertThat(newState.newState.getState("2")).isEqualTo(r2.newState)
        assertThat(newState.newState.getState("3")).isSameAs(initState3)
        assertThat(newState.updatedKeys).hasSize(1)
        assertThat(newState.updatedKeys).contains("2")
    }

    @Test
    fun `get all keys`() {
        val r1 = TestReducer("1")
        val r2 = TestReducer("2")
        val r3 = TestReducer("3")
        val cr = CombinedReducer(listOf(r1, r2, r3))

        assertThat(cr.allKeys).containsExactly("1", "2", "3")
    }

    private class TestReducer(val myKey: String, val newState : String? = "new_state") : Reducer<String>() {
        override fun reduce(oldState: String, action: Action<*>): String? {
            return newState
        }

        override fun getInitialState(): String {
            return ""
        }

        override fun getStateKey(): String = myKey
    }

    /**
     * This test reducer will always return the initial state
     */
    private class TestReducerOldState(val myKey: String, val initState : String) : Reducer<String>() {
        override fun reduce(oldState: String, action: Action<*>): String? {
            return oldState
        }

        override fun getInitialState(): String {
            return initState
        }

        override fun getStateKey(): String = myKey
    }

}