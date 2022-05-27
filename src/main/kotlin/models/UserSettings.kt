package models

import DEFAULT_USER_SETTINGS

data class UserSettings(
    val userId: String,
    var isSlackDigestMuted: Boolean = DEFAULT_USER_SETTINGS.isSlackDigestMuted,
    var isYouTrackMuted: Boolean = DEFAULT_USER_SETTINGS.isYouTrackMuted,
    val mutedYouTrackProjects: Set<String> = DEFAULT_USER_SETTINGS.mutedYouTrackProjects,
    val notifyPriority: Set<TaskPriority> = DEFAULT_USER_SETTINGS.notifyPriority,
    val notifyType: Set<TaskType> = DEFAULT_USER_SETTINGS.notifyType
)