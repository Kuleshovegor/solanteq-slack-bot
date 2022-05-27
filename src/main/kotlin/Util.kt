import models.TaskPriority
import models.UserSettings
import java.util.*

val DAYS_OF_WEEK = mapOf(
    "monday" to Calendar.MONDAY,
    "tuesday" to Calendar.TUESDAY,
    "wednesday" to Calendar.WEDNESDAY,
    "thursday" to Calendar.THURSDAY,
    "friday" to Calendar.FRIDAY,
    "saturday" to Calendar.SATURDAY,
    "sunday" to Calendar.SUNDAY
)
val DAYS_OF_WEEK_TO_STRING = mapOf(
    Calendar.MONDAY to "monday",
    Calendar.TUESDAY to "tuesday",
    Calendar.WEDNESDAY to "wednesday",
    Calendar.THURSDAY to "thursday",
    Calendar.FRIDAY to "friday",
    Calendar.SATURDAY to "saturday",
    Calendar.SUNDAY to "sunday"
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