package com.yeldos.scheduleconverter.service

import com.yeldos.scheduleconverter.domain.fixtures.WeekFixture
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WeekRequestMapperTest {

    private val mapper = WeekRequestMapper()

    @Test
    fun map() {
        val week = mapper.map(WeekScheduleRequestFixture.weekScheduleRequest)
        assertEquals(WeekFixture.week, week)
    }
}