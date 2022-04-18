package handlers.youTrack

import com.slack.api.bolt.context.WebEndpointContext
import com.slack.api.bolt.handler.WebEndpointHandler
import com.slack.api.bolt.request.WebEndpointRequest
import com.slack.api.bolt.response.Response
import com.slack.api.methods.MethodsClient
import org.kodein.di.DI
import org.kodein.di.instance

class SLAHandler(di: DI) : WebEndpointHandler {
    private val client: MethodsClient by di.instance()
    private val token: String by di.instance()

    override fun apply(request: WebEndpointRequest?, context: WebEndpointContext?): Response {
        client.chatPostMessage { r ->
            r.token(token)
                .channel("C037UBC3R0U")
                .text(request?.requestBodyAsString)
        }

        return Response.json(200, "okes")
    }
}