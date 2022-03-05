package dsl.builders

import dsl.BotConfig
import dsl.ChannelDescription
import dsl.DescriptionDsl
import dsl.UserDescription

@DescriptionDsl
class DescriptionBuilder {
    private val channels = mutableMapOf<String, ChannelDescription>()
    private val admins = mutableSetOf<UserDescription>()
    internal var slackBotToken = ""
    internal var teamId = ""

    fun channels(channelsBuilder: ChannelsBuilder.() -> Unit) {
        val channelDescription = ChannelsBuilder().apply(channelsBuilder).build()
        channelDescription.forEach{ channels[it.name] = it}
        channelDescription.forEach { admins.addAll(it.users) }
    }

    fun build(): BotConfig {
        check(slackBotToken.isNotEmpty()) {"Not empty slackBotToken expected."}
        check(teamId.isNotEmpty()) {"Not empty teamId expected."}

        return BotConfig(slackBotToken, teamId, channels, admins)
    }
}