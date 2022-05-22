package models

data class YouTrackMention(val issueId: String, val projectName: String?, val userEmail: String, val link: String) {
    override fun toString(): String {
        return """
            Новое упоминание в YouTrack!
            Проект: $projectName
            Ссылка: $link
        """.trimIndent()
    }
}