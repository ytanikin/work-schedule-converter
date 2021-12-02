package com.yeldos.scheduleconverter.service

import com.yeldos.scheduleconverter.domain.Shift
import com.yeldos.scheduleconverter.domain.Shift.Companion.SIXTY_SECONDS
import com.yeldos.scheduleconverter.domain.exception.BusinessException
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequest
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*


/**
 * Validates the request.
 * First, it sorts a day's values in ascending order.
 * Afterwards it verifies corner-cases:
 * - if each an open hour has a pair of close hour in the same day
 * - if each **last** open hour has a pair of close hour in next day
 * - if each **first** close hour has a pair of close hour in previous
 * - if a day's last open hour is greater than close hour of the **next** day,
 * in order to avoid a user's confusion with a shift like "10 AM - 10:01 AM"
 * except when it comes to midnight, which is a special case e.g. 0:00 - 00:50(or 0:00)
 * will be considered as "12 AM - 11:59 PM"
 * - if the open hour is before the close hour
 *
 * For the rest of the cases refer to [Shift]
 *
 * If there are errors, a RequestException is thrown with all gathered errors.
 */

@Component
class RequestValidator {

    fun validate(request: WeekScheduleRequest) {
        sortDayValue(request)
        val errors: MutableList<String> = verify(request)
        if (errors.isNotEmpty()) {
            throw BusinessException(errors.toList())
        }
    }

    private fun verify(weekSchedule: WeekScheduleRequest): MutableList<String> {
        val errors: MutableList<String> = mutableListOf()
        for (dayMapEntry in weekSchedule.days) {
            val dayHours = dayMapEntry.value
            val dayOfWeek = dayMapEntry.key
            if (dayHours.isNotEmpty()) verifyOverlappingHours(dayHours, errors, dayOfWeek)
            verifyHoursAcrossDays(weekSchedule, errors, dayOfWeek)
        }
        return errors
    }

    /**
     * - Verifies if a day's last hour is  open has a pair of close hour in next day
     * - Verifies if a day's first hour is close has a pair of open hour in previous day
     */
    private fun verifyHoursAcrossDays(weekSchedule: WeekScheduleRequest, errors: MutableList<String>, dayOfWeek: DayOfWeek) {
        val currentDay = weekSchedule.days[dayOfWeek]
        if (currentDay!!.isEmpty()) {
            return
        }
        verifyOpenBeforeCloseHours(errors, weekSchedule, dayOfWeek)
        if (isLastHourClose(currentDay)) {
            return
        }
        verifyCloseAfterOpenHours(errors, weekSchedule, dayOfWeek)
    }

    /**
     * Verifies if a day's last hour is  open has a pair of close hour in next day
     */
    private fun verifyCloseAfterOpenHours(errors: MutableList<String>, weekSchedule: WeekScheduleRequest, weekDay: DayOfWeek) {
        val nextDay = weekSchedule.nextDay(weekDay)
        if (nextDay.isEmpty() || nextDay.first().isOpenType) {
            errors += "Open Hour of ${weekDay.fullDisplayName} must have Close Hour after"
            return
        }
        verifyNextDayCloseHourLessOpenHour(errors, weekDay, nextDay, weekSchedule.days[weekDay]!!)
    }

    /**
     * Verifies if a day's last open hour is greater than close hour of next day, in order to avoid user's confusion
     */
    private fun verifyNextDayCloseHourLessOpenHour(
        errors: MutableList<String>,
        weekDay: DayOfWeek,
        nextDay: List<OpenHoursRequest>,
        currentDay: List<OpenHoursRequest>,
    ) {
        val lastOpenValue = currentDay.last().value!!
        val firstCloseValue = nextDay.first().value!!

        if (lastOpenValue in SIXTY_SECONDS until firstCloseValue) {
            errors += "Open hour of ${weekDay.fullDisplayName} is less than close hour of next day, this can mislead the user, please check your schedule"
        }
        val validations = Shift.validate(lastOpenValue, firstCloseValue, false).map {
            "$it in ${weekDay.fullDisplayName} with values $lastOpenValue and $firstCloseValue"
        }
        errors += validations
    }

    /**
     * Verifies if a day's first hour is close has a pair of open hour in previous day
     */
    private fun verifyOpenBeforeCloseHours(errors: MutableList<String>, weekSchedule: WeekScheduleRequest, dayOfWeek: DayOfWeek) {
        val prevDay = weekSchedule.previousDay(dayOfWeek)
        val currentDay = weekSchedule.days[dayOfWeek]!!
        if (isFirstTypeClose(currentDay) && (prevDay.isEmpty() || !isLastHourOpen(prevDay))) {
            errors += "Close Hour of ${dayOfWeek.fullDisplayName} must have Open Hour before"
        }
    }

    private fun verifyOverlappingHours(dayHours: List<OpenHoursRequest>, errors: MutableList<String>, dayOfWeek: DayOfWeek) {
        for (i in 0 until dayHours.size - 1) {
            val current = dayHours[i]
            val next = dayHours[i + 1]
            verifyIntervalAndOverlaps(current, next, errors, dayOfWeek)
        }
    }

    /**
     * - verifies if values within a day are not equal to each other.
     * - verifies if the interval of opening and closing hours is bigger than 60 seconds
     * - verifies if the open hour is before the close hour
     */
    private fun verifyIntervalAndOverlaps(current: OpenHoursRequest, next: OpenHoursRequest, errors: MutableList<String>, dayOfWeek: DayOfWeek) {
        if (current.type == next.type) {
            errors += "${dayOfWeek.fullDisplayName} has overlapping hours, values: ${current.value}, ${next.value}"
            return
        }
        val validations = Shift.validate(current.value!!, next.value!!, false).map {
            "$it in ${dayOfWeek.fullDisplayName} with values ${current.value} and ${next.value}"
        }
        errors += validations
    }

    private fun sortDayValue(request: WeekScheduleRequest) = request.days.values.forEach { it.sortBy(OpenHoursRequest::value) }

    private fun isLastHourClose(currentDay: List<OpenHoursRequest>): Boolean = currentDay.last().isCloseType

    private fun isLastHourOpen(prevDay: List<OpenHoursRequest>): Boolean = prevDay.last().isOpenType

    private fun isFirstTypeClose(currentDay: MutableList<OpenHoursRequest>): Boolean = currentDay.first().isCloseType

    private val DayOfWeek.fullDisplayName: String
        get() = this.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
}

