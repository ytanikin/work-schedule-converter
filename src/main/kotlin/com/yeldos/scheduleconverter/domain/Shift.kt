package com.yeldos.scheduleconverter.domain

import com.yeldos.scheduleconverter.domain.exception.BusinessException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import kotlin.math.absoluteValue


/**
 * Represents a shift of a schedule.
 *
 * Verifies its correctness during initialization, if there are any errors, an exception is thrown.
 * - if values are not equal to each other in a day except for midnight, allow for zero values.
 * - if the interval of opening and closing hours is greater than 60 seconds except for midnight
 * - if the interval of opening and closing is greater than 2 minutes in case **opening** time is 11:59 PM,
 * require to make it 11:58 PM. 12:00 AM as a closing time is not allowed.
 * - if values are not greater than 24 * 60 * 60 - 1
 * - if values are not negative
 */
data class Shift(private val openingSeconds: Int, private val closingSeconds: Int) {
    init {
        validate(openingSeconds, closingSeconds)
    }

    /**
     * @return The opening and closing time of the shift e.g. "9 AM - 6 PM".
     */
    val shift: String by lazy { "$openingTime - $closingTime" }
    private val openingTime by lazy { convertSecondsToHour(openingSeconds, false) }
    private val closingTime by lazy { convertSecondsToHour(closingSeconds, true) }

    private fun convertSecondsToHour(seconds: Int, closeTime: Boolean): String {
        if (closeTime && isClosingMidnight(closingSeconds)) {
            return convertSecondsToHour(MAX_SECONDS_VALUE)
        }
        return convertSecondsToHour(seconds)
    }

    private fun convertSecondsToHour(seconds: Int): String {
        val instantTime = LocalDate.EPOCH.atTime(LocalTime.ofSecondOfDay(seconds.toLong())).atZone(ZoneId.systemDefault()).toInstant()
        val date = Date.from(instantTime)
        return DATE_FORMAT.format(date).replace(":00", "")
    }

    companion object {
        private val DATE_FORMAT: DateFormat = SimpleDateFormat("h:mm aa")
        const val SIXTY_SECONDS = 60
        const val MAX_SECONDS_VALUE = 24 * SIXTY_SECONDS * SIXTY_SECONDS - 1
        private const val MIN_INTERVAL_SECONDS = SIXTY_SECONDS
        private val VALIDATIONS: List<Pair<(o: Int, c: Int) -> Boolean, String>> = listOf(
            { open: Int, close: Int -> open >= 0 && close >= 0 }
                    to "Time cannot be negative",
            { open: Int, close: Int -> close <= MAX_SECONDS_VALUE && open <= MAX_SECONDS_VALUE }
                    to "Time must be less than $MAX_SECONDS_VALUE",
            { open: Int, close: Int -> open != close || isClosingMidnight(close) }
                    to "Opening time cannot be equal to closing time",
            { open: Int, close: Int -> isIntervalValid(close, open) || isClosingMidnight(close) }
                    to "Interval must be greater than $MIN_INTERVAL_SECONDS seconds",
            { open: Int, close: Int -> isRoundIntervalValid(open, close) }
                    to "Minutes equivalent interval must be greater than 120 seconds in case it is midnight, the time 11:59 PM - 11:59 PM can mislead a user",
        )

        /**
         * Verifies if the shift is valid.
         * The method is used from the constructor and can be called from outside
         * (**poor solution, but necessary to be able to collect all errors before throwing an exception if present**)
         *
         * If there are errors and [throwException] is true, an exception is thrown.
         */
        fun validate(openingSeconds: Int, closingSeconds: Int, throwException: Boolean = true): List<String> {
            val errors = VALIDATIONS.filter { !it.first(openingSeconds, closingSeconds) }.map { it.second }
            if (throwException and errors.isNotEmpty()) {
                throw BusinessException(errors)
            }
            return errors
        }

        private fun isRoundIntervalValid(openingSeconds: Int, closingSeconds: Int): Boolean {
            if (!isClosingMidnight(closingSeconds)) {
                return true
            }
            return openingSeconds <= MAX_SECONDS_VALUE - SIXTY_SECONDS
        }

        private fun isIntervalValid(closingSeconds: Int, openingsSeconds: Int) = (closingSeconds - openingsSeconds).absoluteValue >= MIN_INTERVAL_SECONDS

        private fun isClosingMidnight(closingSeconds: Int) = closingSeconds < SIXTY_SECONDS
    }
}