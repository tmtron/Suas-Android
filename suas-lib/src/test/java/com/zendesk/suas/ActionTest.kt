package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.lang.ClassCastException
import java.util.*

class ActionTest {


    @Test
    fun `action with type only`() {
        val action = Action<Any>("type")

        assertThat(action.actionType).isEqualTo("type")
        assertThat(action.rawData).isNull()

        val erasureAction : Action<*> = action
        assertThat(erasureAction.getData<Any>()).isNull()
        assertThat(erasureAction.getData(Any::class.java)).isNull()
    }

    @Test
    fun `action with data`() {
        val action = Action("type", Date(0))
        assertThat(action.actionType).isEqualTo("type")
        assertThat(action.rawData).isEqualTo(Date(0))

        val erasureAction : Action<*> = action
        assertThat(erasureAction.getData(Date::class.java)).isEqualTo(Date(0))
        assertThat(erasureAction.getData<Date>()).isEqualTo(Date(0))
    }

    @Test(expected = ClassCastException::class)
    fun `action with data - unsafe get data`() {
        val action : Action<*> = Action<Any>("type", Date(0))
        assertThat(action.actionType).isEqualTo("type")
        val string: String? = action.getData()
        assertThat(string).isNull()
    }

    @Test
    fun `action with data - safe get data`() {
        val action : Action<*> = Action<Any>("type", Date(0))
        assertThat(action.actionType).isEqualTo("type")
        assertThat(action.getData(String::class.java)).isNull()
    }
}