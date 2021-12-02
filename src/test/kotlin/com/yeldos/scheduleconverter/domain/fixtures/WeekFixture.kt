package com.yeldos.scheduleconverter.domain.fixtures

import com.yeldos.scheduleconverter.domain.Week
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.closedMonday
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.closedTuesday
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.friday8AMto6PM
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.saturday8AMto6PM
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.sunday8AMto6PM
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.thursday8AMto6PM
import com.yeldos.scheduleconverter.domain.fixtures.DayFixture.wednesday8AMto6PM

object WeekFixture {
    val week by lazy { Week(closedMonday, closedTuesday, wednesday8AMto6PM, thursday8AMto6PM, friday8AMto6PM, saturday8AMto6PM, sunday8AMto6PM) }
}