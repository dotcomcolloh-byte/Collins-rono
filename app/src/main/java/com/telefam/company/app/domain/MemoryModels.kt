package com.telefam.company.app.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class MemoryMediaType {
    IMAGE,
    VIDEO
}

enum class MemoryPrivacy(val wireName: String) {
    ONLY_ME("ONLY_ME"),
    FRIENDS("FRIENDS"),
    PUBLIC("PUBLIC");

    companion object {
        fun fromWire(wire: String?): MemoryPrivacy {
            return when (wire?.uppercase(Locale.ROOT)) {
                "FRIENDS" -> FRIENDS
                "PUBLIC" -> PUBLIC
                else -> ONLY_ME
            }
        }
    }
}

enum class MemoryFilterTab {
    ALL,
    ON_THIS_DAY,
    HIGHLIGHTS,
    BOOKMARKED
}

enum class MemoryViewMode {
    GRID,
    TIMELINE
}

data class MemoryItem(
    val id: String,
    val userId: String,
    val caption: String,
    val mediaUri: String,
    val mediaType: MemoryMediaType = MemoryMediaType.IMAGE,
    val memoryDateMs: Long,
    val createdAtMs: Long,
    val locationName: String = "",
    val tagFeeling: String = "",
    val isFavorite: Boolean = false,
    val privacy: MemoryPrivacy = MemoryPrivacy.ONLY_ME,
    val yearsAgo: Int = 0,
    val formattedDate: String = "",
    val isOnThisDay: Boolean = false
) {
    companion object {
        fun formatMemoryDate(dateMs: Long): String {
            val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            return sdf.format(Date(dateMs))
        }

        fun calculateYearsAgo(memoryDateMs: Long, nowMs: Long = System.currentTimeMillis()): Int {
            val memCal = Calendar.getInstance().apply { timeInMillis = memoryDateMs }
            val nowCal = Calendar.getInstance().apply { timeInMillis = nowMs }
            val diff = nowCal.get(Calendar.YEAR) - memCal.get(Calendar.YEAR)
            return diff.coerceAtLeast(0)
        }

        fun checkIfOnThisDay(memoryDateMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
            val memCal = Calendar.getInstance().apply { timeInMillis = memoryDateMs }
            val nowCal = Calendar.getInstance().apply { timeInMillis = nowMs }
            val sameMonth = memCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
            val sameDay = memCal.get(Calendar.DAY_OF_MONTH) == nowCal.get(Calendar.DAY_OF_MONTH)
            val differentYear = memCal.get(Calendar.YEAR) < nowCal.get(Calendar.YEAR)
            return sameMonth && sameDay && differentYear
        }
    }
}
