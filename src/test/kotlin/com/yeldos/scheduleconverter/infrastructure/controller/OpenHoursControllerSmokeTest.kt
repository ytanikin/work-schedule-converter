package com.yeldos.scheduleconverter.infrastructure.controller

import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture
import com.yeldos.scheduleconverter.scheduleText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
internal class OpenHoursControllerSmokeTest {

    @Autowired
    private lateinit var controller: OpenHoursController

    @Test
    fun formatTimeRequest() {
        val weekSchedule = controller.formatTimeRequestToHumanReadable(WeekScheduleRequestFixture.weekScheduleRequest)
        assertEquals(weekSchedule, scheduleText)
    }
}