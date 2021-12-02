package com.yeldos.scheduleconverter.infrastructure.controller.request


private const val OPEN = "open"
private const val CLOSE = "close"

private const val SECONDS_IN_HOUR = 3600

object OpenHoursRequestFixture {
    val close11AM by lazy { closeAt(0) }
    val open9AM by lazy { openAt( 9 * SECONDS_IN_HOUR) }
    val close6PM by lazy { closeAt( 18 * SECONDS_IN_HOUR) }
    val open8AM by lazy { openAt(8 * SECONDS_IN_HOUR) }
    val open6PM by lazy { openAt(18 * SECONDS_IN_HOUR) }
    fun openAt(seconds: Int) = OpenHoursRequest(OPEN, seconds)
    fun closeAt(seconds: Int) = OpenHoursRequest(CLOSE, seconds)
}