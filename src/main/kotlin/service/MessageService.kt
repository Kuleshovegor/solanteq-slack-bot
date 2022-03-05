package service

import models.Message
import repository.SlackMessageRepository
import repository.UserRepository

class MessageService(
    private val messageRepository: SlackMessageRepository,
    private val userRepository: UserRepository
) {


    fun addMessage(message: Message) {
        messageRepository.addMessage(message)
    }

    fun creatUserDigest(userId: String): String {
        val userChannels = userRepository.getChannelsByUserId(userId) ?: error("user not found")
        val result = StringBuilder()
        result.append("У вас не отвеченные сообщения в чатах поодержки.")
            .append(System.lineSeparator())
            .append(System.lineSeparator())
        userChannels.channels.forEach { channel ->
            val messages = messageRepository.getMessagesByChannelId(channel.id)
            val links = messages.joinToString(System.lineSeparator()) { it.link }

            result.append("В канале ${channel.name}:").append(System.lineSeparator())
            result.append(links).append(System.lineSeparator())
        }

        return result.toString()
    }

    fun getAllMessages(): List<Message> {
        return messageRepository.getMessages()
    }

    fun deleteMessage(ts: String) {
        messageRepository.deleteMessage(ts)
    }

}