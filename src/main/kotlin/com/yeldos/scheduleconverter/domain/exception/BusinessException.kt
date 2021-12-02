package com.yeldos.scheduleconverter.domain.exception

class BusinessException(val errors: List<String>) : RuntimeException(errors.toString())
