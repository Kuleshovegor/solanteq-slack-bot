package handlers.youTrack

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.slack.api.bolt.context.WebEndpointContext
import com.slack.api.bolt.handler.WebEndpointHandler
import com.slack.api.bolt.request.WebEndpointRequest
import com.slack.api.bolt.response.Response
import models.YouTrackComment
import org.kodein.di.DI
import org.kodein.di.instance
import service.MessageService
import service.UserService
import service.YouTrackCommentService

class NewYouTrackComment(di: DI): WebEndpointHandler {
    private val userService: UserService by di.instance()
    private val messageService: MessageService by di.instance()
    private val youTrackCommentService: YouTrackCommentService by di.instance()

    override fun apply(request: WebEndpointRequest?, context: WebEndpointContext?): Response {
        if (request == null || context == null) {
            return Response.error(500)
        }

        val comment = jacksonObjectMapper().readValue<YouTrackComment>(request.requestBodyAsString)

        youTrackCommentService.save(comment)

        messageService.sendMessage(userService.getUserIdByEmail(comment.userEmail), comment.toString())

        return Response.ok("keks")
    }

}