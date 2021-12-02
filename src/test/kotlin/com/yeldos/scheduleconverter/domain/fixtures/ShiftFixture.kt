package com.yeldos.scheduleconverter.domain.fixtures

import com.yeldos.scheduleconverter.domain.Shift

object ShiftFixture {
    val shift8AMto6PM by lazy { Shift(8 * 3600, 18 * 3600) }
    val shift8AMto9PM by lazy { Shift(8 * 3600, 21 * 3600) }
    val shift0AMto11PM by lazy { Shift(0 * 3600, 23 * 3600) }
}