package handlers.commands

import com.slack.api.bolt.context.builtin.SlashCommandContext
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.request.builtin.SlashCommandRequest
import com.slack.api.bolt.response.Response
import com.slack.api.methods.MethodsClient
import org.kodein.di.DI
import org.kodein.di.instance
import service.SlackBlockService

class MenuHandler(di: DI): SlashCommandHandler {
    private val slackBlockService: SlackBlockService by di.instance()
    private val client: MethodsClient by di.instance()
    private val token: String by di.instance("SLACK_BOT_TOKEN")

    override fun apply(req: SlashCommandRequest, context: SlashCommandContext): Response {
        println(slackBlockService.getAdminMenu().toString())

        val resp = context.respond(listOf(slackBlockService.getAdminMenu()))

        println(resp)
        return context.ack()
    }
}