package com.yeldos.scheduleconverter.infrastructure.controller.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yeldos.scheduleconverter.domain.Shift.Companion.MAX_SECONDS_VALUE
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

private const val ERROR_THE_VALUE_RANGE = "The value must be between 0 and $MAX_SECONDS_VALUE"
private const val ERROR_MESSAGE_OPEN_OR_CLOSE = "The type must be open or close"
private const val OPEN = "open"
private const val CLOSE = "close"

data class OpenHoursRequest(
        @field:[NotNull Pattern(regexp = "$OPEN|$CLOSE", message = ERROR_MESSAGE_OPEN_OR_CLOSE)]
        val type: String?,

        @field:[NotNull Max(value = MAX_SECONDS_VALUE.toLong(), message = ERROR_THE_VALUE_RANGE) Min(value = 0, message = ERROR_THE_VALUE_RANGE)]
        val value: Int?
) {
    @get:JsonIgnore
    val isCloseType = type == CLOSE
    @get:JsonIgnore
    val isOpenType = type == OPEN
}