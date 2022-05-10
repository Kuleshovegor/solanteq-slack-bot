package client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import models.YouTrackAuthor
import models.YouTrackComment2
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


/*
Упоминание считается отвеченным, если
пользователь из команды проекта выполнил одно из действий:
* ответил в комментариях, done
* поставил реакцию на комментарий, impossible
* изменил задачу. done
*/