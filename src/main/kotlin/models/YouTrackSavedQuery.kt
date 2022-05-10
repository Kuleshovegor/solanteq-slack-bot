package models

data class YouTrackSavedQuery(
    val name: String,
    val id: String,
    val issues: List<YouTrackIssueAndFields>?
) {

    override fun toString(): String{
        return "----------------$name-----------------\n" +
                "id: $id\n${issues?.joinToString("\n-------\n")}\n\n\n"
    }
}
