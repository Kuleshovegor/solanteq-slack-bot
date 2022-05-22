package models

data class YouTrackCommentInput(
    val id: String,
    val author: YouTrackAuthor?,
    val text: String?,
    val created: Long?,
    val updated: Long?
)
