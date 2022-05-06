package handlers.youTrack

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.slack.api.bolt.context.WebEndpointContext
import com.slack.api.bolt.handler.WebEndpointHandler
import com.slack.api.bolt.request.WebEndpointRequest
import com.slack.api.bolt.response.Response
import models.YouTrackMention
import org.kodein.di.DI
import org.kodein.di.instance
import service.MessageService
import service.UserService
import service.YouTrackCommentService

class NewYouTrackMentionComment(di: DI) : WebEndpointHandler {
    private val userService: UserService by di.instance()
    private val messageService: MessageService by di.instance()
    private val youTrackCommentService: YouTrackCommentService by di.instance()

    override fun apply(request: WebEndpointRequest?, context: WebEndpointContext?): Response {
        if (request == null || context == null) {
            return Response.error(500)
        }

        if (request.clientIpAddress != System.getenv("YOU_TRACK_IP")) {
            return Response.error(405)
        }

        val mention = jacksonObjectMapper().readValue<YouTrackMention>(request.requestBodyAsString)

        youTrackCommentService.save(mention)

        messageService.sendMessage(userService.getUserIdByEmail(mention.userEmail), mention.toString())

        return Response.ok()
    }

}