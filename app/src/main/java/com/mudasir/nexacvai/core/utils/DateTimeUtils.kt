package com.mudasir.nexacvai.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    /**
     * Converts an epoch timestamp into a clean, localized relative time string format.
     * Examples: "Updated recently", "Updated 5m ago", "Updated 2h ago", "Updated 3d ago"
     */
    fun getRelativeTimeSpanString(timeMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timeMillis
        
        if (diff < 0) return "recently"
        
        return when {
            diff < 60_000L -> "recently"
            diff < 3600_000L -> {
                val minutes = diff / 60_000L
                "${minutes}m ago"
            }
            diff < 86400_000L -> {
                val hours = diff / 3600_000L
                "${hours}h ago"
            }
            diff < 2592000_000L -> { // Less than 30 days
                val days = diff / 86400_000L
                if (days == 1L) "1 day ago" else "${days} days ago"
            }
            else -> {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                "on ${sdf.format(Date(timeMillis))}"
            }
        }
    }
}
