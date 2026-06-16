package com.vcyberpunk.notes.presentation.utils

import android.content.Context
import android.icu.text.DateFormat
import com.vcyberpunk.notes.R
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object DateFormatter {

    private val MILLIS_IN_HOUR = TimeUnit.HOURS.toMillis(1)
    private val MILLIS_IN_DAY = TimeUnit.DAYS.toMillis(1)
    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun formatDateToString(context: Context, timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < MILLIS_IN_HOUR -> context.getString(R.string.date_just_now)
            diff < MILLIS_IN_DAY -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                context.resources.getQuantityString(
                    R.plurals.date_hours_ago,
                    hours.toInt(),
                    hours
                )
            }
            else -> {
                formatter.format(timestamp)
            }

        }
    }

}