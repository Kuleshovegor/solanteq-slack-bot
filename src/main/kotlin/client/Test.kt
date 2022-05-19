package client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import models.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val mapper = jacksonObjectMapper()

// from YouTrackSettings or some config:
val accessToken = "perm:dmFscnVuMA==.NDgtMA==.IJoW7PYApOrjlKShjCKTV6L635ImFw"
val youTrackName = "solanteqtestbot.youtrack.cloud"

fun main() {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    println( hasCommentBeenAnswered(YouTrackComment("DEMO-34", "karaseva@niuitmo.ru"), SupportChannel("", "", "", setOf("valrun0@ya.ru"))))
//    hasCommentBeenAnswered(YouTrackComment("2-185", "Kuleshovegor2001@gmail.com"), SupportChannel("", "", "", setOf("valrun0@ya.ru")))
//    println(getSLA(listOf("sla", "Назначенные на меня")).joinToString("\n\n"))
}

//TODO supportChannel.supportUserIds is not EMAIL
fun hasCommentBeenAnswered(comment: YouTrackComment, supportChannel: SupportChannel): Boolean {
    val comments = getComments(comment.issueId)
    val userLogin = getLogin(comment.userEmail)

    val (lastCommentOfUserIndex, lastCommentOfUser) = comments.withIndex()
        .findLast { (it.value.author?.email ?: "") == comment.userEmail }!!

//  check reply Comment
    if (comments.drop(lastCommentOfUserIndex + 1).any {
            (it.text != null
                    && it.text.contains("@$userLogin")
                    && supportChannel.supportUserIds.contains(it.author?.login ?: ""))
        }) return true

//  check Reaction
    val reactions = getReactionsAuthorsEmail(comment.issueId, lastCommentOfUser.id)
    if (!supportChannel.supportUserIds.any { reactions.contains(it) }) return true

//    check update Issue
    val (issueUpdate, issueUpdater) = getLastUpdateInfo(comment.issueId)
    return (lastCommentOfUser.created != null && issueUpdate != null && lastCommentOfUser.created < issueUpdate)
            && (issueUpdater != null && supportChannel.supportUserIds.contains(issueUpdater))
}

private fun getComments(issueId: String): List<YouTrackComment2> {
    val jsonStr = youTrackRequest("issues/$issueId/comments?fields=id,author%28login,email%29,text")
    return mapper.readValue(jsonStr)
}

private fun getReactionsAuthorsEmail(issueId: String, commentId: String): List<String> {
    val jsonStr = youTrackRequest("issues/$issueId/comments/$commentId/reactions/?fields=author%28login,email%29")
    data class Reaction(val author: YouTrackAuthor)
    return mapper.readValue<List<Reaction>>(jsonStr).map { it.author.email?: "" }
}

private fun getLogin(userEmail: String): String {
    val jsonStr = youTrackRequest("users?fields=login,email")
    val listUsers = mapper.readValue<List<YouTrackAuthor>>(jsonStr)
    return listUsers.find { it.email == userEmail }?.login ?: ""
}

private fun getLastUpdateInfo(issueId: String): Pair<Long?, String?> {
    val jsonStr = youTrackRequest("issues/$issueId?fields=updated,updater%28login,email%29")
    data class Update(val updated: Long?, val updater: YouTrackAuthor?)

    val update = mapper.readValue<Update>(jsonStr)
    return Pair(update.updated, update.updater?.email)
}

fun getSLA(namesSLA: List<String>): List<Pair<String, List<YouTrackIssue>?>> {
    val jsonStr = youTrackRequest(
        "savedQueries?fields=id,name,issues" +
                "%28id,summary,customFields" +
                "%28name,value%28id,name%29" +
                "%29%29"
    )
    val savedQueries: List<YouTrackSavedQuery> = mapper.readValue(jsonStr.replace("[]", "null"))

    return savedQueries.filter { namesSLA.contains(it.name) }.map { sq ->
        val list = sq.issues?.map { issue ->
            val priority = issue.customFields?.find { it.name == "Priority" }?.value?.name ?: ""
            val type = issue.customFields?.find { it.name == "Type" }?.value?.name ?: ""
            YouTrackIssue(issue.id, issue.summary, type, priority, "https://$youTrackName/issue/${issue.id}")
        }
        Pair(sq.name, list)
    }
}

private fun youTrackRequest(requestString: String): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://$youTrackName/api/$requestString"))
        .headers("Authorization", "Bearer $accessToken", "Accept", "application/json")
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}
