package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import org.kodein.di.DI
import org.kodein.di.instance
import service.DigestService

class DigestCommandHandler(di: DI) : SlashCommandHandler {
    private val digestService: DigestService by di.instance()

    override fun apply(req: SlashCommandRequest?, context: SlashCommandContext?): Response {
        if (req == null || context == null) {
            return Response.error(500)
        }

        val response = digestService.sendUserDigest(req.payload.userId)

        if (!response.isOk) {
            context.logger.error(response.error)
            return context.ack("Some error has occurred. The digest was not sent.")
        }

        return context.ack("The digest was sent.")
    }
}