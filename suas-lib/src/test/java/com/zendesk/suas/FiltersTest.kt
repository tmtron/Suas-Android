package com.zendesk.suas

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*


class FiltersTest {

    @Test
    fun `default filter test`() {
        val filter = Filters.DEFAULT

        val result = filter.filter(Date(1), Date(2))

        assertThat(result).isTrue()
    }

    @Test
    fun `equals filter test`() {
        val filter = Filters.EQUALS

        assertThat(filter.filter(Date(1), Date(2))).isTrue()
        assertThat(filter.filter(Date(1), Date(1))).isFalse()

        assertThat(filter.filter("abc", Date(1))).isTrue()
    }
}