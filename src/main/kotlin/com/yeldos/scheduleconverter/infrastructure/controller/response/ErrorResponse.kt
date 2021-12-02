package com.yeldos.scheduleconverter.infrastructure.controller.response

class ErrorResponse(val errors: List<String>) {
    constructor(errorMessage: String) : this(listOf(errorMessage))
}