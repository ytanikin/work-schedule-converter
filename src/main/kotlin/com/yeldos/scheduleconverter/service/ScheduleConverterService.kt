package com.yeldos.scheduleconverter.service

import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import com.yeldos.scheduleconverter.domain.Week
import org.springframework.stereotype.Service

/**
 * Validates the request and converts it to a [Week] object.
 */
@Service
class ScheduleConverterService(private val requestValidator: RequestValidator, private val requestMapper: RequestMapper) {
    fun format(request: WeekScheduleRequest): String {
        requestValidator.validate(request)
        val week: Week = requestMapper.map(request)
        return week.schedule
    }
}