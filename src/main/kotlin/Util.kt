import models.TaskPriority
import models.UserSettings
import java.util.*

val DAYS_OF_WEEK = mapOf(
    "понедельник" to Calendar.MONDAY,
    "вторник" to Calendar.TUESDAY,
    "среда" to Calendar.WEDNESDAY,
    "четверг" to Calendar.THURSDAY,
    "пятница" to Calendar.FRIDAY,
    "суббота" to Calendar.SATURDAY,
    "воскресенье" to Calendar.SUNDAY
)
val DAYS_OF_WEEK_TO_STRING = mapOf(
    Calendar.MONDAY to "понедельник",
    Calendar.TUESDAY to "вторник",
    Calendar.WEDNESDAY to "среда",
    Calendar.THURSDAY to "четверг",
    Calendar.FRIDAY to "пятница",
    Calendar.SATURDAY to "суббота",
    Calendar.SUNDAY to "воскресенье"
)

val DEFAULT_USER_SETTINGS = UserSettings(
    userId = "",
    isSlackDigestMuted = false,
    isYouTrackMuted = false,
    mutedYouTrackProjects = setOf(),
    notifyPriority = setOf(
        TaskPriority.Minor,
        TaskPriority.Normal,
        TaskPriority.Major,
        TaskPriority.Critical,
        TaskPriority.ShowStopper
    )
)