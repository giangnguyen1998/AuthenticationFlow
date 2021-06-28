package edu.nuce.apps.newflowtypes.shared.util

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

fun ZonedDateTime.toEpochMilli() = this.toInstant().toEpochMilli()

fun String.toLongDate(): Long {
    val zonedDateTime = ZonedDateTime.parse(this, DateTimeFormatter.ISO_INSTANT)
    return zonedDateTime.toEpochMilli()
}