package com.yeldos.scheduleconverter.infrastructure.controller

import com.yeldos.scheduleconverter.domain.exception.BusinessException
import com.yeldos.scheduleconverter.infrastructure.controller.request.WeekScheduleRequest
import com.yeldos.scheduleconverter.infrastructure.controller.response.ErrorResponse
import com.yeldos.scheduleconverter.service.ScheduleConverterService
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/schedule")
class OpenHoursController(private val scheduleConverterService: ScheduleConverterService) {

    @PostMapping("format")
    fun formatTimeRequestToHumanReadable(@RequestBody @Valid request: WeekScheduleRequest): String {
        return scheduleConverterService.format(request)
    }

    /**
     * Handles Business validation errors.
     */
    @ExceptionHandler(BusinessException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleBusinessExceptions(exception: BusinessException): ErrorResponse = ErrorResponse(exception.errors)

    /**
     * Handles Jakarta Bean Validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleBeanValidationExceptions(exception: MethodArgumentNotValidException): ErrorResponse {
        return ErrorResponse(exception.bindingResult.allErrors.map(::formatBeanValidationError))
    }

    private fun formatBeanValidationError(o: ObjectError) = "${(o as FieldError).field} = ${o.rejectedValue}, ${o.getDefaultMessage()}"
}