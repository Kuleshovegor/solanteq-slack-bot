import com.slack.api.bolt.context.WebEndpointContext
import com.slack.api.bolt.handler.WebEndpointHandler
import com.slack.api.bolt.request.WebEndpointRequest
import com.slack.api.bolt.response.Response
import com.slack.api.methods.MethodsClient
import dsl.BotConfig

class Handler(private val client: MethodsClient, private val botConfig: BotConfig) : WebEndpointHandler {
    override fun apply(request: WebEndpointRequest?, context: WebEndpointContext?): Response {
        client.chatPostMessage { r ->
            r.token(botConfig.slackBotToken)
                .channel("U033D8BJH0C")
                .text("hi")
        }

        return Response.json(200, "okes")
    }
}