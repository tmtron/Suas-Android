package zendesk.suas

import org.junit.Test
import org.mockito.Mockito.*

class ListenerComponentTest {

    @Test
    fun `listener for component`() {
        val oldState = listOf("1", "2", "3")
        val newState = listOf("a", "b", "c")

        val component = spy(TestComponent())
        val componentListener: Listener<List<String>> = Listeners.create(component)

        componentListener.update(newState)

        verify(component, times(1)).update("a, b, c")
    }

    @Test
    fun `listener for component - selector returns null`() {
        val oldState = listOf("1", "2", "3")
        val newState = listOf("a", "b", "c")

        val component = spy(TestComponent(nullSelector =  true))
        val componentListener: Listener<List<String>> = Listeners.create(component)

        componentListener.update(newState)

        verify(component, never()).update(anyString())
    }

    open class TestComponent(val nullSelector: Boolean = false) : Component<List<String>, String> {
        override fun update(e: String) {

        }

        override fun getSelector(): StateSelector<List<String>, String> = StateSelector { state ->
            if (nullSelector) null
            else state.reduce { a, b -> "$a, $b" }
        }
    }

}