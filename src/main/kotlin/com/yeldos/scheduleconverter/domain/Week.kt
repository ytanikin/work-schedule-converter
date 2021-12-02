package com.yeldos.scheduleconverter.domain

import java.time.DayOfWeek

data class Week(val monday: Day, val tuesday: Day, val wednesday: Day, val thursday: Day, val friday: Day, val saturday: Day, val sunday: Day) {
    init {
        require(monday.dayOfWeek == DayOfWeek.MONDAY) { "Monday must be first day of week" }
        require(tuesday.dayOfWeek == DayOfWeek.TUESDAY) { "Tuesday must be second day of week" }
        require(wednesday.dayOfWeek == DayOfWeek.WEDNESDAY) { "Wednesday must be third day of week" }
        require(thursday.dayOfWeek == DayOfWeek.THURSDAY) { "Thursday must be fourth day of week" }
        require(friday.dayOfWeek == DayOfWeek.FRIDAY) { "Friday must be fifth day of week" }
        require(saturday.dayOfWeek == DayOfWeek.SATURDAY) { "Saturday must be sixth day of week" }
        require(sunday.dayOfWeek == DayOfWeek.SUNDAY) { "Sunday must be seventh day of week" }
    }

    private val weekDays: List<Day> by lazy { listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday) }

    val schedule by lazy { weekDays.joinToString(separator = System.lineSeparator()) { it.daySchedule } }

    companion object {
        fun from(days: Map<DayOfWeek, Day>): Week {
            require(days.size == 7) { "Week must contain 7 days" }
            return Week(
                    monday = days[DayOfWeek.MONDAY]!!,
                    tuesday = days[DayOfWeek.TUESDAY]!!,
                    wednesday = days[DayOfWeek.WEDNESDAY]!!,
                    thursday = days[DayOfWeek.THURSDAY]!!,
                    friday = days[DayOfWeek.FRIDAY]!!,
                    saturday = days[DayOfWeek.SATURDAY]!!,
                    sunday = days[DayOfWeek.SUNDAY]!!
            )
        }
    }
}