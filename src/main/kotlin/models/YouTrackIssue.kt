package models

data class YouTrackIssue(
    val id: String,
    val summary: String,
    val type: String,
    val priority: String,
    val link: String
)
