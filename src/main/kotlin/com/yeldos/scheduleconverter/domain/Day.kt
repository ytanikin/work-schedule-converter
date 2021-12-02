package com.yeldos.scheduleconverter.domain

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

private const val CLOSED_MARK = "Closed"

data class Day(val dayOfWeek: DayOfWeek, private val shifts: List<Shift>) {
    val daySchedule: String by lazy {
        val weekName = "${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}: "
        weekName + if (shifts.isEmpty()) CLOSED_MARK else appendOpenHours()
    }

    private fun appendOpenHours(): String = shifts.joinToString { it.shift }

}