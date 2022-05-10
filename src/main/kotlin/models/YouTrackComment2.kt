package models

data class YouTrackComment2(
    val id: String,
    val author: YouTrackAuthor?,
    val text: String?,
    val created: Long?,
    val updated: Long?
)
