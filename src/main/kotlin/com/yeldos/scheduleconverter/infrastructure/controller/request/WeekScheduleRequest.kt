package com.yeldos.scheduleconverter.infrastructure.controller.request

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.DayOfWeek
import java.time.DayOfWeek.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * WeekScheduleRequest for converting week schedule to timetable
 *
 * Properties are nullable to be able to gather null fields from request and send back to client with explanation
 */
data class WeekScheduleRequest(
        @field:[Valid NotNull] val monday: List<OpenHoursRequest?>?,
        @field:[Valid NotNull] val tuesday: List<OpenHoursRequest?>?,
        @field:[Valid NotNull] val wednesday: List<OpenHoursRequest?>?,
        @field:[Valid NotNull] val thursday: List<OpenHoursRequest?>?,
        @field:[Valid NotNull] val friday: List<OpenHoursRequest?>?,
        @field:[Valid NotNull] val saturday: List<OpenHoursRequest?>?,
        @field:[Valid NotNull] val sunday: List<OpenHoursRequest?>?,
) {

    @get:JsonIgnore
    val days by lazy {
        mapOf(
                Pair(MONDAY, toNonNull(monday)),
                Pair(TUESDAY, toNonNull(tuesday)),
                Pair(WEDNESDAY, toNonNull(wednesday)),
                Pair(THURSDAY, toNonNull(thursday)),
                Pair(FRIDAY, toNonNull(friday)),
                Pair(SATURDAY, toNonNull(saturday)),
                Pair(SUNDAY, toNonNull(sunday))
        )
    }

    /**
     * @return next day of the week after given day [current],
     * or the first day of the week if the given day is last day of the week.
     */
    fun nextDay(current: DayOfWeek): List<OpenHoursRequest> = days[(current.plus(1))]!!

    /**
     * @return previous day of the week before the given day [current],
     * or last day of the week if the given day is first day of week.
     */
    fun previousDay(current: DayOfWeek): List<OpenHoursRequest> = days[(current.minus(1))]!!

    private fun toNonNull(nullableReq: List<OpenHoursRequest?>?) = nullableReq!!.map { it!! }.toMutableList()

}