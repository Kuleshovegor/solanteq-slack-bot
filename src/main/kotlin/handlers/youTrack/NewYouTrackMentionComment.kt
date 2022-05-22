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
import org.slf4j.LoggerFactory
import service.MessageService
import service.UserService
import service.UserSettingsService
import service.YouTrackCommentService

class NewYouTrackMentionComment(di: DI) : WebEndpointHandler {
    private val userService: UserService by di.instance()
    private val messageService: MessageService by di.instance()
    private val youTrackCommentService: YouTrackCommentService by di.instance()
    private val logger = LoggerFactory.getLogger("NewYouTrackMentionComment")

    override fun apply(request: WebEndpointRequest, context: WebEndpointContext): Response {
        if (request.clientIpAddress != System.getenv("YOU_TRACK_IP")) {
            return Response.error(405)
        }

        val mention = jacksonObjectMapper().readValue<YouTrackMention>(request.requestBodyAsString)

        youTrackCommentService.save(mention)

        try {
            val userId = userService.getUserIdByEmail(mention.userEmail) ?: return Response.error(400)
            if (!userService.isYouTrackUserMuted(userId, mention.projectName!!)) {
                messageService.sendMessage(userId, mention.toString())
            }
        } catch (e: Exception) {
            logger.error(e.message)
            return Response.error(500)
        }

        return Response.ok()
    }

}