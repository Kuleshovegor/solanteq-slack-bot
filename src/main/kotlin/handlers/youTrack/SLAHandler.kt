package handlers.youTrack

import com.slack.api.bolt.context.WebEndpointContext
import com.slack.api.bolt.handler.WebEndpointHandler
import com.slack.api.bolt.request.WebEndpointRequest
import com.slack.api.bolt.response.Response
import com.slack.api.methods.MethodsClient
import dsl.BotConfig

class SLAHandler(private val client: MethodsClient, private val botConfig: BotConfig) : WebEndpointHandler {
    override fun apply(request: WebEndpointRequest?, context: WebEndpointContext?): Response {

        println(request?.requestBodyAsString)

        client.chatPostMessage { r ->
            r.token(botConfig.slackBotToken)
                .channel("C037UBC3R0U")
                .text(request?.requestBodyAsString)
        }

        return Response.json(200, "okes")
    }
}