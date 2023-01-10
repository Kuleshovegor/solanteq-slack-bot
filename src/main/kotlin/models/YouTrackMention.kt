package models

data class YouTrackMention(val issueId: String, val projectName: String, val userEmail: String, val link: String) {
    val id = link.substring(link.lastIndexOf("#comment=") + 9)

    override fun toString(): String {
        return """
            New mention in YouTrack!
            Project: $projectName
            Link: $link
        """.trimIndent()
    }
}