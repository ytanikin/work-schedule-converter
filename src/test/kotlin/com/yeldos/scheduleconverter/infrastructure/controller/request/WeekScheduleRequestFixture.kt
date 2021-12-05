package com.yeldos.scheduleconverter.infrastructure.controller.request

import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture.close6PM
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture.closeAt
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture.open8AM
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture.openAt

object WeekScheduleRequestFixture {
    val weekScheduleRequest by lazy { weekScheduleRequest() }
    val nullMonday by lazy { weekScheduleRequest(mondayHours = null) }
    val negativeValueMonday by lazy { weekScheduleRequest(mondayHours = mutableListOf(openAt(-1))) }
    val incorrectType by lazy {
        weekScheduleRequest(tuesdayHours = mutableListOf(
            OpenHoursRequest("not open", 1),
            OpenHoursRequest("not close", 100))
        )
    }
    val incorrectTypeAndNegativeNullMonday by lazy {
        weekScheduleRequest(
            mondayHours = null,
            tuesdayHours = mutableListOf(OpenHoursRequest("not open", 1), closeAt(-200))
        )
    }
    val midnightClose by lazy {
        weekScheduleRequest(
            tuesdayHours = mutableListOf(openAt(40)),
            wednesdayHours = mutableListOf(closeAt(50))
        )
    }

    val midnightOpenAndClose by lazy {
        weekScheduleRequest(
            tuesdayHours = mutableListOf(openAt(24 * 60 * 60 - 5)),
            wednesdayHours = mutableListOf(closeAt(50))
        )
    }

    val closeOfNextDayGreaterThanOpen by lazy {
        weekScheduleRequest(
            tuesdayHours = mutableListOf(openAt(1800)),
            wednesdayHours = mutableListOf(closeAt(1840))
        )
    }

    fun weekScheduleRequest(
        mondayHours: MutableList<OpenHoursRequest?>? = mutableListOf(),
        tuesdayHours: MutableList<OpenHoursRequest?>? = mutableListOf(),
        wednesdayHours: MutableList<OpenHoursRequest?>? = mutableListOf(open8AM, close6PM, closeAt(10 * 60 * 60), openAt(12 * 60 * 60)),
        thursdayHours: MutableList<OpenHoursRequest?>? = mutableListOf(open8AM, close6PM),
        fridayHours: MutableList<OpenHoursRequest?>? = mutableListOf(open8AM, close6PM),
        saturdayHours: MutableList<OpenHoursRequest?>? = mutableListOf(open8AM, close6PM),
        sundayHours: MutableList<OpenHoursRequest?>? = mutableListOf(open8AM, close6PM),
    ): WeekScheduleRequest = WeekScheduleRequest(mondayHours, tuesdayHours, wednesdayHours, thursdayHours, fridayHours, saturdayHours, sundayHours)

}