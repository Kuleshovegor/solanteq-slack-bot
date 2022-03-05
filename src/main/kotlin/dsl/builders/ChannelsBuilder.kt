package dsl.builders

import dsl.DescriptionDsl
import dsl.ChannelDescription

@DescriptionDsl
class ChannelsBuilder {
    private val channels = mutableListOf<ChannelDescription>()

    fun channel(name: String, channelBuilder: ChannelBuilder.() -> Unit) {
        channels.add(ChannelBuilder(name).apply(channelBuilder).build())
    }

    fun build(): List<ChannelDescription> {
        return channels
    }
}