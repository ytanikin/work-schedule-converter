package com.yeldos.scheduleconverter.domain

import com.yeldos.scheduleconverter.domain.fixtures.DayFixture
import com.yeldos.scheduleconverter.domain.fixtures.WeekFixture
import com.yeldos.scheduleconverter.scheduleText
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class WeekTest {

    @Test
    fun getSchedule() {
        assertEquals(scheduleText, WeekFixture.week.schedule)
    }

    @Test
    fun `exception while initialize, Tuesday instead of Monday`() {
        val exception = assertThrows(IllegalArgumentException::class.java) { mixedMondayAndTuesday() }
        assertEquals("Monday must be first day of week", exception.message)
    }

    private fun mixedMondayAndTuesday() {
        Week(
            DayFixture.closedTuesday,
            DayFixture.closedMonday,
            DayFixture.wednesday8AMto6PM,
            DayFixture.thursday8AMto6PM,
            DayFixture.friday8AMto6PM,
            DayFixture.saturday8AMto6PM,
            DayFixture.sunday8AMto6PM
        )
    }


}