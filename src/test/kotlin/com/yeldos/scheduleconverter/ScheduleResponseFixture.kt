package com.yeldos.scheduleconverter


val scheduleText by lazy {
     """|Monday: Closed
        |Tuesday: Closed
        |Wednesday: 8 AM - 6 PM
        |Thursday: 8 AM - 6 PM
        |Friday: 8 AM - 6 PM
        |Saturday: 8 AM - 6 PM
        |Sunday: 8 AM - 6 PM""".trimMargin().replace("\n", System.lineSeparator())
}