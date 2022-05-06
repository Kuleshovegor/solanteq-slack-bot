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
import org.slf4j.LoggerFactory
import service.YouTrackCommentService

class NewYouTrackComment(di: DI) : WebEndpointHandler {
    private val youTrackCommentService: YouTrackCommentService by di.instance()

    override fun apply(request: WebEndpointRequest?, context: WebEndpointContext?): Response {
        if (request == null || context == null) {
            return Response.error(500)
        }
        if (request.clientIpAddress != System.getenv("YOU_TRACK_IP")) {
            return Response.error(405)
        }


        val comment = jacksonObjectMapper().readValue<YouTrackComment>(request.requestBodyAsString)

        youTrackCommentService.deleteMentions(comment)

        return Response.ok()
    }

}