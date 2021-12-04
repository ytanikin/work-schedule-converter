package com.yeldos.scheduleconverter.service

import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import com.yeldos.scheduleconverter.domain.Week
import org.springframework.stereotype.Service

/**
 * Validates the request and converts it to a [Week] object.
 */
@Service
class ScheduleConverterService(private val weekRequestValidator: WeekRequestValidator, private val weekRequestMapper: WeekRequestMapper) {
    fun format(request: WeekScheduleRequest): String {
        weekRequestValidator.validate(request)
        val week: Week = weekRequestMapper.map(request)
        return week.schedule
    }
}