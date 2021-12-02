package com.yeldos.scheduleconverter.service

import com.yeldos.scheduleconverter.domain.Day
import com.yeldos.scheduleconverter.domain.Shift
import com.yeldos.scheduleconverter.domain.Week
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequest
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import org.springframework.stereotype.Component
import java.time.DayOfWeek

/**
 * Mapper for converting request objects to domain objects.
 * - Maps [WeekScheduleRequest] object to a [Week] object.
 * - Creates a [Day] for each day of [WeekScheduleRequest].
 * - Creates a [Shift] object for each [OpenHoursRequest] pair of the same or next day.
 * - If there is no [OpenHoursRequest] for a day, initializes a [Day] object with an empty list of [Shift]
 */
@Component
class RequestMapper {
    fun map(request: WeekScheduleRequest): Week {
        val weekDays: Map<DayOfWeek, Day> = DayOfWeek.values().associateBy({ it }, {
            Day(it, collectShifts(it, request))
        })
        return Week.from(weekDays)
    }

    private fun collectShifts(dayOfWeek: DayOfWeek, weekRequest: WeekScheduleRequest): List<Shift> {
        val day: List<OpenHoursRequest> = weekRequest.days[dayOfWeek]!!
        return if (day.isEmpty()) emptyList() else getOpenCloseHours(weekRequest, dayOfWeek, day)
    }

    private fun getOpenCloseHours(weekRequest: WeekScheduleRequest, weekDay: DayOfWeek, day: List<OpenHoursRequest>): List<Shift> {
        val shifts: MutableList<Shift> = getDayShifts(day)
        if (isLastHourOpen(day)) {
            shifts += withCloseHourFromNextDay(weekRequest, weekDay, day)
        }
        return shifts.toList()
    }

    private fun getDayShifts(day: List<OpenHoursRequest>): MutableList<Shift> {
        val shifts: MutableList<Shift> = mutableListOf()
        var hourIndex = if (day.first().isCloseType) 1 else 0
        while (hourIndex + 1 < day.size) {
            val openHour: OpenHoursRequest = day[hourIndex]
            val closeHour: OpenHoursRequest = day[hourIndex + 1]
            shifts += Shift(openHour.value!!, closeHour.value!!)
            hourIndex += 2
        }
        return shifts
    }

    private fun withCloseHourFromNextDay(weekRequest: WeekScheduleRequest, weekDay: DayOfWeek, day: List<OpenHoursRequest>): Shift {
        val nextDay: List<OpenHoursRequest> = weekRequest.nextDay(weekDay)
        return Shift(day.last().value!!, nextDay.first().value!!)
    }

    private fun isLastHourOpen(day: List<OpenHoursRequest>): Boolean = day.last().isOpenType
}