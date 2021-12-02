package com.yeldos.scheduleconverter.domain

import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.closedMonday
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.closedTuesday
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.wednesday8AMto6PM
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

internal class DayTest {

    @Test
    fun getDaySchedule() {
        assertEquals("Monday: Closed", closedMonday.daySchedule)
        assertEquals("Tuesday: Closed", closedTuesday.daySchedule)
        assertEquals("Wednesday: 8 AM - 6 PM", wednesday8AMto6PM.daySchedule)
    }
}