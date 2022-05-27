package handlers.youTrack

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.slack.api.bolt.context.WebEndpointContext
import com.slack.api.bolt.handler.WebEndpointHandler
import com.slack.api.bolt.request.WebEndpointRequest
import com.slack.api.bolt.response.Response
import models.NewTask
import org.kodein.di.DI
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import service.MessageService
import service.UserService
import java.net.InetAddress
import java.net.URL

class NewTaskHandler(di: DI) : WebEndpointHandler {
    private val userService: UserService by di.instance()
    private val messageService: MessageService by di.instance()
    private val youTrackWorkflow: String by di.instance("YOUTRACK_WORKFLOW_IP")
    private val mapper = jacksonObjectMapper()
    private val logger = LoggerFactory.getLogger("newYouTrackTask")

    override fun apply(request: WebEndpointRequest, context: WebEndpointContext): Response {
        if (request.clientIpAddress != youTrackWorkflow) {
            return Response.error(405)
        }

        val newTask = mapper.readValue<NewTask>(request.requestBodyAsString)

        if (newTask.ownerEmail == null) {
            logger.error("New task $newTask email is null.")
            return Response.error(400)
        }


        try {
            val userId = userService.getUserIdByEmail(newTask.ownerEmail) ?: return Response.ok()
            if (!userService.isYouTrackUserMuted(userId, newTask.projectName!!)) {
                messageService.sendMessage(userId, newTask.toString())
            }
        } catch (e: Exception) {
            logger.error("Error while finding user ID by email. ${e.message}")

            return Response.error(500)
        }

        if (newTask.assigneeEmail != null && newTask.assigneeEmail != newTask.ownerEmail) {
            try {
                val assigneeUserId = userService.getUserIdByEmail(newTask.assigneeEmail) ?: return Response.ok()
                if (!userService.isYouTrackUserMuted(assigneeUserId, newTask.projectName)) {
                    messageService.sendMessage(assigneeUserId, newTask.toString())
                }
            } catch (e: Exception) {
                logger.error("Error while finding user ID by email. ${e.message}")

                return Response.error(500)
            }
        }

        return Response.ok()
    }

}