package client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import models.*
import org.kodein.di.DI
import org.kodein.di.instance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

//fun main() {
//    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//    val mention = YouTrackMention(
//        issueId = "DEMO-135",
//        projectName = "Демопроект",
//        userEmail = "kuleshovegor2001@gmail.com",
//        link = "https://solanteqtestbot.youtrack.cloud:443/issue/DEMO-135#comment=4-126"
//    )
//    println(mention.id)
//    println(getComment(mention.issueId, mention.id))
//    println(getComments(mention.issueId))
//    println(getReactionsAuthorsEmail(mention.issueId, mention.id))
//    println(hasCommentBeenAnswered(mention))
//    println(youTrackRequest("issues/DEMO-1000").body())
//    println( hasCommentBeenAnswered(YouTrackMention("DEMO-34", "Демопроект", "karaseva@niuitmo.ru", "sfdsdf")))
//    hasCommentBeenAnswered(YouTrackComment("2-185", "Kuleshovegor2001@gmail.com"), SupportChannel("", "", "", setOf("valrun0@ya.ru")))
//    println(getSLA(listOf("sla", "Назначенные на меня")).joinToString("\n\n"))
//}

class YouTrackClient(di: DI) {
    private val accessToken: String by di.instance("YOUTRACK_ACCESS_TOKEN")
    private val youTrackURL: String by di.instance("YOUTRACK_URL")
    private val mapper = jacksonObjectMapper()

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun hasCommentBeenAnswered(youTrackMention: YouTrackMention): Boolean {
        val comment = getComment(youTrackMention.issueId, youTrackMention.id) ?: return true
        val comments = getComments(youTrackMention.issueId)

        val indexMention = comments.indexOfFirst { it.id == comment.id }

//  check reply Comment
        if (comments.drop(indexMention + 1).any {
                (it.text != null
                        && it.author?.email?.lowercase(Locale.getDefault()) == youTrackMention.userEmail)
            }) return true

//  check Reaction
        val reactions = getReactionsAuthorsEmail(youTrackMention.issueId, comment.id)
        if (reactions.map { it.lowercase(Locale.getDefault()) }.contains(youTrackMention.userEmail)) return true

//    check update Issue
        val (issueUpdate, issueUpdater) = getLastUpdateInfo(youTrackMention.issueId)

        if (comment.created != null && issueUpdate != null && comment.created < issueUpdate
            && issueUpdater != null && youTrackMention.userEmail == issueUpdater.lowercase(Locale.getDefault())
        ) {
            return true
        }

        return false
    }

    private fun getComment(issueId: String, commentId: String): YouTrackCommentInput? {
        val response =
            youTrackRequest("issues/$issueId/comments/$commentId?fields=id,created,author%28login,email%29,text")

        if (response.statusCode() == 404) {
            return null
        }
        if (response.statusCode() != 200) {
            throw UnexpectedStatusCodeException("Request: ${"issues/$issueId/comments/$commentId"} Response code= ${response.statusCode()} Response body: ${response.body()}")
        }

        return mapper.readValue(response.body())
    }

    private fun getComments(issueId: String): List<YouTrackCommentInput> {
        val response = youTrackRequest("issues/$issueId/comments?fields=id,created,author%28login,email%29,text")
        if (response.statusCode() == 404) {
            return listOf()
        }
        if (response.statusCode() != 200) {
            throw UnexpectedStatusCodeException("Request: ${"issues/$issueId"} Response code= ${response.statusCode()} Response body: ${response.body()}")
        }
        return mapper.readValue(response.body())
    }

    //
    private fun getReactionsAuthorsEmail(issueId: String, commentId: String): List<String> {
        val jsonStr = youTrackRequest("issues/$issueId/comments/$commentId/reactions/?fields=author%28login,email%29")

        data class Reaction(val author: YouTrackAuthor)
        return mapper.readValue<List<Reaction>>(jsonStr.body()).map { it.author.email ?: "" }
    }

    //
//private fun getLogin(userEmail: String): String {
//    val jsonStr = youTrackRequest("users?fields=login,email")
//    val listUsers = mapper.readValue<List<YouTrackAuthor>>(jsonStr)
//    return listUsers.find { it.email == userEmail }?.login ?: ""
//}
//
    private fun getLastUpdateInfo(issueId: String): Pair<Long?, String?> {
        val response = youTrackRequest("issues/$issueId?fields=updated,updater%28login,email%29")
        if (response.statusCode() != 200) {
            throw UnexpectedStatusCodeException("Request: ${"issues/$issueId?fields=updated,updater%28login,email%29"} Response code= ${response.statusCode()} Response body: ${response.body()}")
        }
        data class Update(val updated: Long?, val updater: YouTrackAuthor?)

        val update = mapper.readValue<Update>(response.body())
        return Pair(update.updated, update.updater?.email)
    }

    fun getSLA(namesSLA: List<String>): List<Pair<String, List<YouTrackIssue>?>> {
        val response = youTrackRequest(
            "savedQueries?fields=id,name,issues" +
                    "%28id,summary,customFields" +
                    "%28name,value%28id,name%29" +
                    "%29%29"
        )

        if (response.statusCode() != 200) {
            throw UnexpectedStatusCodeException(
                "Request: ${
                    "savedQueries?fields=id,name,issues" +
                            "%28id,summary,customFields" +
                            "%28name,value%28id,name%29" +
                            "%29%29"
                } Response code= ${response.statusCode()} Response body: ${response.body()}"
            )
        }

        val savedQueries: List<YouTrackSavedQuery> = mapper.readValue(response.body().replace("[]", "null"))

        return savedQueries.filter { namesSLA.contains(it.name) }.map { sq ->
            val list = sq.issues?.map { issue ->
                val priority = issue.customFields?.find { it.name == "Priority" }?.value?.name ?: ""
                val type = issue.customFields?.find { it.name == "Type" }?.value?.name ?: ""
                YouTrackIssue(issue.id, issue.summary, type, priority, "$youTrackURL/issue/${issue.id}")
            }
            Pair(sq.name, list)
        }
    }

    private fun youTrackRequest(requestString: String): HttpResponse<String> {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$youTrackURL/api/$requestString"))
            .headers("Authorization", "Bearer $accessToken", "Accept", "application/json")
            .build()

        return client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}
