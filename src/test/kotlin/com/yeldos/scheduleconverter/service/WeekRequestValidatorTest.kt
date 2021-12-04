package com.yeldos.scheduleconverter.service

import com.yeldos.scheduleconverter.domain.exception.BusinessException
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequest
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture.closeOfNextDayGreaterThanOpen
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture.weekScheduleRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream


internal class WeekRequestValidatorTest {

    private val validator = WeekRequestValidator()

    @Test
    fun successValidation() {
        validator.validate(weekScheduleRequest)
    }

    @ParameterizedTest
    @ArgumentsSource(WeekScheduleArgumentsProvider::class)
    fun invalidRequestTest(arguments: ParametrizedArguments) {
        val exception = assertThrows(BusinessException::class.java) {
            validator.validate(arguments.weekScheduleRequest)
        }
        assertEquals(arguments.messages, exception.errors)
    }

    class WeekScheduleArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            val equalValues = Arguments.of(ParametrizedArguments(
                openHours = mutableListOf(OpenHoursRequestFixture.close6PM, OpenHoursRequestFixture.close6PM),
                messages = listOf("Monday has overlapping hours, values: 64800, 64800", "Close Hour of Monday must have Open Hour before")
            ))
            val openHasNoPair = Arguments.of(ParametrizedArguments(
                openHours = mutableListOf(OpenHoursRequestFixture.close6PM),
                messages = listOf("Close Hour of Monday must have Open Hour before")
            ))
            val closeHasNoPair = Arguments.of(ParametrizedArguments(
                openHours = mutableListOf(OpenHoursRequestFixture.open9AM),
                messages = listOf("Open Hour of Monday must have Close Hour after")
            ))
            val typesAreMixedUp = Arguments.of(ParametrizedArguments(
                openHours = mutableListOf(OpenHoursRequestFixture.open6PM, OpenHoursRequestFixture.close11AM),
                messages = listOf("Close Hour of Monday must have Open Hour before", "Open Hour of Monday must have Close Hour after")
            ))
            val smallInterval = Arguments.of(ParametrizedArguments(
                openHours = mutableListOf(OpenHoursRequestFixture.openAt(3600), OpenHoursRequestFixture.closeAt(3650)),
                messages = listOf("Interval must be greater than 60 seconds in Monday with values 3600 and 3650")
            ))
            val closeOfNextDayGreaterThanOpen = Arguments.of(ParametrizedArguments(
                weekScheduleRequest = closeOfNextDayGreaterThanOpen,
                messages = listOf("Open hour of Tuesday is less than close hour of next day, this can mislead the user, please check your schedule", "Interval must be greater than 60 seconds in Tuesday with values 1800 and 1840")
            ))
            return Stream.of(equalValues, openHasNoPair, closeHasNoPair, typesAreMixedUp, smallInterval, closeOfNextDayGreaterThanOpen)
        }
    }

    /**
     * The class to hold arguments for parameterized test, in order to make parameterized test strong typed as well:)
     */
    class ParametrizedArguments(
        openHours: MutableList<OpenHoursRequest?> = mutableListOf(),
        val weekScheduleRequest: WeekScheduleRequest = weekScheduleRequest(mondayHours = openHours),
        val messages: List<String>,
    )

}