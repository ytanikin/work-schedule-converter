package com.yeldos.scheduleconverter.domain.fixtures

import com.yeldos.scheduleconverter.domain.Day
import com.yeldos.scheduleconverter.domain.fixtures.ShiftFixture.shift8AMto6PM
import java.time.DayOfWeek

object DayFixture {
    val closedMonday: Day by lazy { Day(DayOfWeek.MONDAY, emptyList()) }
    val closedTuesday: Day by lazy { Day(DayOfWeek.TUESDAY, emptyList()) }
    val wednesday8AMto6PM: Day by lazy { Day(DayOfWeek.WEDNESDAY, listOf(shift8AMto6PM)) }
    val thursday8AMto6PM: Day by lazy { Day(DayOfWeek.THURSDAY, listOf(shift8AMto6PM)) }
    val friday8AMto6PM: Day by lazy { Day(DayOfWeek.FRIDAY, listOf(shift8AMto6PM)) }
    val saturday8AMto6PM: Day by lazy { Day(DayOfWeek.SATURDAY, listOf(shift8AMto6PM)) }
    val sunday8AMto6PM: Day by lazy { Day(DayOfWeek.SUNDAY, listOf(shift8AMto6PM)) }
}