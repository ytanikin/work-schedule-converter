package com.yeldos.scheduleconverter.service

import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import com.yeldos.scheduleconverter.domain.Week
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequest
import org.springframework.stereotype.Service

/**
 * Validates the request and converts it to a [Week] object.
 */
@Service
class ScheduleConverterService(private val weekRequestValidator: WeekRequestValidator, private val weekRequestMapper: WeekRequestMapper) {
    fun format(request: WeekScheduleRequest): String {
        sortDayValue(request)
        weekRequestValidator.validate(request)
        val week: Week = weekRequestMapper.map(request)
        return week.schedule
    }
    private fun sortDayValue(request: WeekScheduleRequest) = request.days.values.forEach { it.sortBy(OpenHoursRequest::value) }
}