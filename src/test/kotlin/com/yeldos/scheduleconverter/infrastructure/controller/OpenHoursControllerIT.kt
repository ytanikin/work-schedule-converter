package com.yeldos.scheduleconverter.infrastructure.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture.close6PM
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture.closeAt
import com.yeldos.scheduleconverter.infrastructure.controller.request.OpenHoursRequestFixture.openAt
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture.closeOfNextDayGreaterThanOpen
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture.incorrectTypeAndNegativeNullMonday
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequestFixture.weekScheduleRequest
import com.yeldos.scheduleconverter.infrastructure.controller.response.ErrorResponse
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.stream.Stream


@SpringBootTest
@AutoConfigureMockMvc
class OpenHoursControllerIT {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val humanHoursEndpoint = "/schedule/format"

    @ParameterizedTest
    @ArgumentsSource(ValidRequestHumanScheduleArgumentsProvider::class)
    fun validInput(request: WeekScheduleRequest, schedule: String) {
        mockMvc.post(humanHoursEndpoint) {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            content { string(schedule) }
        }
    }

    class ValidRequestHumanScheduleArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            val tuesdayTillMidnight = of(WeekScheduleRequestFixture.midnightClose, tuesdayToMidnight)
            val from6AmTo8PM = of(weekScheduleRequest, expectedScheduleText)
            return Stream.of(tuesdayTillMidnight, from6AmTo8PM)
        }
    }

    @ParameterizedTest(name = "{index} => {2}")
    @ArgumentsSource(RequestErrorResponseArgumentsProvider::class)
    fun testInvalidRequest(request: WeekScheduleRequest, response: ErrorResponse, description: String) {
        mockMvc.post(humanHoursEndpoint) {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content {
                json(objectMapper.writeValueAsString(response))
            }
        }
    }

    class RequestErrorResponseArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments?>? {
            val incorrectTypeAndNegativeNullMonday = of(incorrectTypeAndNegativeNullMonday, ErrorResponse(listOf(
                "monday = null, must not be null",
                "tuesday[1].value = -200, The value must be between 0 and 86399",
                "tuesday[0].type = not open, The type must be open or close"
            )), "Bean Validation missing fields validation")

            val incorrectType = of(WeekScheduleRequestFixture.incorrectType, ErrorResponse(listOf(
                "tuesday[1].type = not close, The type must be open or close",
                "tuesday[0].type = not open, The type must be open or close"
            )), "Bean Validation missing fields validation")

            val negativeValueMonday = of(WeekScheduleRequestFixture.negativeValueMonday,
                ErrorResponse("monday[0].value = -1, The value must be between 0 and 86399"),
                "Bean Validation negative value")

            val nullMonday = of(WeekScheduleRequestFixture.nullMonday, ErrorResponse("monday = null, must not be null"),
                "Bean Validation monday is missing")

            val incorrectInterval = of(weekScheduleRequest(mondayHours = mutableListOf(openAt(3600), closeAt(3650))),
                ErrorResponse("Interval must be greater than 60 seconds in Monday with values 3600 and 3650"),
                "Interval is less than 60 seconds")

            val midnightOpenAndClose = of(WeekScheduleRequestFixture.midnightOpenAndClose, ErrorResponse(listOf(
                "Minutes equivalent interval must be greater than 120 seconds in case it is midnight, the time 11:59 PM - 11:59 PM can mislead a user in Tuesday with values 86395 and 50"
            )), "Midnight open and close")

            val closeOfNextDayGreaterThanOpen = of(closeOfNextDayGreaterThanOpen, ErrorResponse(listOf(
                "Open hour of Tuesday is less than close hour of next day, this can mislead the user, please check your schedule",
                "Interval must be greater than 60 seconds in Tuesday with values 1800 and 1840")),
                "Close of next day greater than open")

            val overlappingHours = of(weekScheduleRequest(mondayHours = mutableListOf(close6PM, close6PM)), ErrorResponse(listOf(
                "Monday has overlapping hours, values: 64800, 64800",
                "Close Hour of Monday must have Open Hour before")),
                "Overlapping hours with equal values")

            return Stream.of(
                incorrectTypeAndNegativeNullMonday,
                incorrectType,
                negativeValueMonday,
                nullMonday,
                midnightOpenAndClose,
                incorrectInterval,
                closeOfNextDayGreaterThanOpen,
                overlappingHours
            )
        }
    }

    companion object {

        private val tuesdayToMidnight by lazy {
            """ |Monday: Closed
                |Tuesday: 12 AM - 11:59 PM
                |Wednesday: Closed
                |Thursday: 8 AM - 6 PM
                |Friday: 8 AM - 6 PM
                |Saturday: 8 AM - 6 PM
                |Sunday: 8 AM - 6 PM""".trimMargin().replaceLineSeparator()
        }

        private val expectedScheduleText by lazy {
             """ |Monday: Closed
                 |Tuesday: Closed
                 |Wednesday: 8 AM - 10 AM, 12 PM - 6 PM
                 |Thursday: 8 AM - 6 PM
                 |Friday: 8 AM - 6 PM
                 |Saturday: 8 AM - 6 PM
                 |Sunday: 8 AM - 6 PM""".trimMargin().replaceLineSeparator()
        }
        private fun String.replaceLineSeparator() = this.replace("\n", System.lineSeparator())

    }

}