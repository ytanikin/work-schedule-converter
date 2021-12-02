package com.yeldos.scheduleconverter.domain

import com.yeldos.scheduleconverter.domain.exception.BusinessException
import com.yeldos.scheduleconverter.domain.fixtures.ShiftFixture.shift0AMto11PM
import com.yeldos.scheduleconverter.domain.fixtures.ShiftFixture.shift8AMto6PM
import com.yeldos.scheduleconverter.domain.fixtures.ShiftFixture.shift8AMto9PM
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

internal class ShiftTest {

    @Test
    fun shiftTest() {
        assertEquals("8 AM - 6 PM", shift8AMto6PM.shift)
        assertEquals("8 AM - 9 PM", shift8AMto9PM.shift)
        assertEquals("12 AM - 11 PM", shift0AMto11PM.shift)
        assertEquals("12 AM - 11:59 PM", Shift(0, 11).shift)
        assertEquals("12:01 AM - 11:59 PM", Shift(100, 0).shift)
        assertEquals("12:01 AM - 11:59 PM", Shift(100, 1).shift)
        assertEquals("12:01 AM - 11:59 PM", Shift(100, 30).shift)
        assertEquals("12:01 AM - 11:59 PM", Shift(100, 86399).shift)
    }

    @ParameterizedTest
    @ArgumentsSource(IncorrectArgumentsProvider::class)
    fun invalidShiftCreationTest(openingTime: Int, closingTime: Int, expectedErrors: List<String>) {
        val exception = assertThrows(BusinessException::class.java) {
            Shift(openingTime, closingTime)
        }
        assertEquals(expectedErrors, exception.errors)
    }

    class IncorrectArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(-1, 1, listOf("Time cannot be negative")),
                Arguments.of(0, 87000, listOf("Time must be less than 86399")),
                Arguments.of(1800, 1850, listOf("Interval must be greater than 60 seconds")),
                Arguments.of(86398, 86399, listOf("Interval must be greater than 60 seconds")),
                Arguments.of(36000, 36000, listOf("Opening time cannot be equal to closing time", "Interval must be greater than 60 seconds")),
                Arguments.of(86398, 0,
                    listOf("Minutes equivalent interval must be greater than 120 seconds in case it is midnight, the time 11:59 PM - 11:59 PM can mislead a user"))
            )
        }
    }
}
