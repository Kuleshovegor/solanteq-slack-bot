package client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import models.YouTrackComment2
import models.YouTrackIssue
import models.YouTrackSavedQuery
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val mapper = jacksonObjectMapper()

// there are arguments:
// from YouTrackSettings or some config:
val accessToken = "perm:dmFscnVuMA==.NDgtMA==.IJoW7PYApOrjlKShjCKTV6L635ImFw"
val youTrackName = "solanteqtestbot.youtrack.cloud"

// from YouTrackComment
val issueId = "2-185"
val userEmail = "Kuleshovegor2001@gmail.com"
val userLogin = "root" // maybe change email to login?
// end arguments

fun main() {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val comments = getComments()

    val (lastCommentIndex, lastComment) = comments.withIndex().findLast { (it.value.author?.email ?: "") == userEmail }!!
    val replyComment = comments.drop(lastCommentIndex + 1).any {
//      This is FALSE, I need help with SupportChannel
        (it.text != null
                && it.text.contains("@$userLogin")
                && (it.author?.login ?: "") != userLogin)
    }
    if (replyComment) println("ОТВЕЧЕННЫЙ КОММЕНТАРИЙ")


    val issueUpdated = getLastUpdateIssue() ?: return
    if (lastComment.created != null && lastComment.created < issueUpdated) println("Обновили задачу")

}

fun getComments(): List<YouTrackComment2> {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://$youTrackName/api/issues/$issueId/comments?fields=id,author%28login,email%29,text"))
        .headers("Authorization", "Bearer $accessToken", "Accept", "application/json")
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val jsonStr = response.body()
    val comments: List<YouTrackComment2> = mapper.readValue(jsonStr)
//    println(jsonStr)
//    println(comments.joinToString("\n"))

    return comments
}

//val nameSLA = "sla"
fun getSLA(nameSLA: String): List<List<YouTrackIssue>?> {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://$youTrackName/api/savedQueries?fields=id,name,issues%28" +
                "id,summary,customFields%28" +
                "name,value%28id,name%29" +
                "%29" +
                "%29"))
        .headers("Authorization", "Bearer $accessToken", "Accept", "application/json")
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val jsonStr = response.body()
    val savedQueries: List<YouTrackSavedQuery> = mapper.readValue(jsonStr.replace("[]","null"))
    return savedQueries.filter { it.name == "sla" }.map { sq ->
        sq.issues?.map { issue ->
            val priority = issue.customFields?.find { it.name == "Priority" }?.value?.name ?: ""
            val type = issue.customFields?.find { it.name == "Type" }?.value?.name ?: ""
            YouTrackIssue(issue.id, issue.summary, type, priority,"https://$youTrackName/issue/${issue.id}")
        }
    }
}

data class Update(val updated: Long?)

fun getLastUpdateIssue(): Long? {
//    Хорошо ещё проверить, что это сделал кто-то из команды
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://$youTrackName/api/issues/$issueId?fields=updated"))
        .headers("Authorization", "Bearer $accessToken", "Accept", "application/json")
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val jsonStr = response.body()

    return mapper.readValue<Update>(jsonStr).updated
}

