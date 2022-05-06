package models

import java.lang.Integer.min

data class NewTask(
    val id: String,
    val ownerEmail: String?,
    val priority: String?,
    val summary: String?,
    val description: String?
) {

    override fun toString(): String {
        return """
            Новая задача в YouTrack!
            Заголовок: ${summary ?: ""}
            Приоритет: ${priority ?: ""}
            Описание: ${description?.substring(0..min(description.length - 1, 50))}
        """.trimIndent()
    }
}