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
import service.MessageService
import service.UserService

class NewTaskHandler(di: DI) : WebEndpointHandler {
    private val userService: UserService by di.instance()
    private val messageService: MessageService by di.instance()

    override fun apply(request: WebEndpointRequest?, context: WebEndpointContext?): Response {
        if (request == null || context == null) {
            return Response.error(500)
        }

        val newTask = jacksonObjectMapper().readValue<NewTask>(request.requestBodyAsString)

        messageService.sendMessage(userService.getUserIdByEmail(newTask.ownerEmail!!), newTask.toString())

        return Response.ok("okes")
    }

}