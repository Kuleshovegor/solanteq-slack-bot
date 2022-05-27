package models

import java.lang.Integer.min

data class NewTask(
    val id: String,
    val ownerEmail: String?,
    val projectName: String?,
    val priority: String?,
    val summary: String?,
    val description: String?,
    val assigneeEmail: String?
) {

    override fun toString(): String {
        return """
            New Task in YouTrack!
            Title: ${summary ?: ""}
            Priority: ${priority ?: ""}
            Assignee: ${assigneeEmail ?: ""}
            Summary: ${
            (description?.substring(
                0..min(
                    description.length - 1,
                    50
                )
            ) + (if (description != null && description.length > 50) "..." else ""))
        }
        """.trimIndent()
    }
}