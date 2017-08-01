package zendesk.suas

import com.google.common.truth.Truth
import com.google.common.truth.Truth.*
import org.junit.Test
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.Future

class StateTest {

    @Test
    fun `get state for key`() {
        val state = State()
        state.updateKey("begin of time", Date(0))

        val date: Any? = state.getState("begin of time")
        assertThat(date).isEqualTo(Date(0))

        val invalid: Any? = state.getState("invalid key")
        assertThat(invalid).isNull()
    }

    @Test
    fun `get state for class`() {
        val state = State(mapOf(State.keyForClass(List::class.java) to 1L))
        state.updateKey(Date::class.java, Date(0))
        state.updateKey(String::class.java, "test")

        val string: String? = state.getState(String::class.java)
        assertThat(string).isEqualTo("test")

        val date: Date? = state.getState(Date::class.java)
        assertThat(date).isEqualTo(Date(0))

        val list: List<*>? = state.getState(List::class.java)
        assertThat(list).isNull()

        val future: Future<*>? = state.getState(Future::class.java)
        assertThat(future).isNull()
    }

    @Test
    fun `get state for key and cast to class`() {
        val state = State()
        state.updateKey("some_date", Date(0))
        state.updateKey("a_string", "test")
        state.updateKey("lol", 1L)

        val date: Date? = state.getState("some_date", Date::class.java)
        assertThat(date).isEqualTo(Date(0))

        val string: String? = state.getState("a_string", String::class.java)
        assertThat(string).isEqualTo("test")

        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        val long: java.lang.Long? = state.getState("lol", java.lang.Long::class.java)
        assertThat(long).isEqualTo(1L)

        val notADate: TimeZone? = state.getState("some_date", TimeZone::class.java)
        assertThat(notADate).isNull()

        val notAString: Date? = state.getState("a_string", Date::class.java)
        assertThat(notAString).isNull()
    }

    @Test
    fun `copy state`() {
        val state = State(mapOf("1" to 1, "2" to 2))

        val newState = state.copy()

        assertThat(newState.getState("1")).isEqualTo(1)
        assertThat(newState.getState("2")).isEqualTo(2)
        assertThat(state.equals(newState)).isTrue()
    }

    @Test
    fun `equals`() {
        val data = mapOf("1" to 1, "2" to 2)
        val state1 = State(data)
        val state2 = State(data)

        assertThat(state1.equals(state2)).isTrue()
        assertThat(state1.hashCode()).isEqualTo(state2.hashCode())
    }

    @Test
    fun `merge stats`() {
        val emptyState = State(
                mapOf(
                        "1" to Date(1),
                        "2" to "test",
                        "3" to BigDecimal(10)
                )
        )

        val partialState = State(
                mapOf(
                        "3" to BigDecimal(100)
                )
        )

        val newState = State.mergeStates(emptyState, partialState)

        assertThat(newState.getState("1")).isEqualTo(Date(1))
        assertThat(newState.getState("2")).isEqualTo("test")
        assertThat(newState.getState("3")).isEqualTo(BigDecimal(100))
    }

}