package models

data class YouTrackIssueAndFields(
    val id: String,
    val summary: String,
    val customFields: List<YouTrackFields>?
)

data class YouTrackFields(
    val name: String,
    val value: YouTrackValue?
) {

    override fun toString(): String {
        return "\n$name\n$value\n"
    }
}

data class YouTrackValue (
    val name: String?,
    val id: String
)
