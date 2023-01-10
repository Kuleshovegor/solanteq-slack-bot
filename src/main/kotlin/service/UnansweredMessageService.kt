package service

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.SlackApiException
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest.ConversationsHistoryRequestBuilder
import com.slack.api.model.Message
import com.slack.api.model.Reaction
import models.UnansweredMessage
import org.kodein.di.DI
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import repository.UnansweredMessageRepository
import java.io.IOException


class UnansweredMessageService(di: DI) {
    private val unansweredMessageRepository: UnansweredMessageRepository by di.instance()
    private val supportChannelService: SupportChannelService by di.instance()
    private val client: MethodsClient by di.instance("slackClient")
    private val token: String by di.instance("SLACK_BOT_TOKEN")
    private val logger = LoggerFactory.getLogger("slack-app")!!

    fun getMessage(channelId: String, ts: String): Message? {
        try {
            val result = client.conversationsHistory { r: ConversationsHistoryRequestBuilder ->
                r
                    .token(token)
                    .channel(channelId) // In a more realistic app, you may store ts data in a db
                    .latest(ts) // Limit results
                    .inclusive(true)
                    .limit(1)
            }
            if (!result.isOk) {
                logger.error(result.error)
                return null
            }
            val message = result.messages[0]
            logger.info(message.toString())
            logger.info("result {}", message.text)
            return message
        } catch (e: IOException) {
            logger.error("error: {}", e.message, e)
        } catch (e: SlackApiException) {
            logger.error("error: {}", e.message, e)
        }
        return null
    }

    fun getCommentMessages(channelId: String, ts: String): List<Message> {
        try {
            val result = client.conversationsReplies { r ->
                r
                    .token(token)
                    .channel(channelId)
                    .ts(ts)
            }

            if (!result.isOk) {
                logger.error(result.error)
                return listOf()
            }

            val messages = result.messages
            logger.info(messages.toString())

            return messages
        } catch (e: IOException) {
            logger.error("error: {}", e.message, e)
        } catch (e: SlackApiException) {
            logger.error("error: {}", e.message, e)
        }

        return listOf()
    }

    fun getReactions(channelId: String, ts: String): List<Reaction> {
        try {
            val result = client.reactionsGet { r ->
                r
                    .token(token)
                    .channel(channelId)
                    .timestamp(ts)
            }

            if (!result.isOk) {
                logger.error(result.error)
                return listOf()
            }

            val reactions = result.message.reactions
            logger.info(reactions.toString())

            return reactions
        } catch (e: IOException) {
            logger.error("error: {}", e.message, e)
        } catch (e: SlackApiException) {
            logger.error("error: {}", e.message, e)
        }

        return listOf()
    }

    fun fetchAllMessages() {
        unansweredMessageRepository.getMessages().forEach {
            getMessage(it.channelId, it.ts)
        }
    }

    fun updateMessage(channelId: String, ts: String) {
        if (getMessage(channelId, ts) == null) {
            deleteMessage(ts)
            return
        }
        val comments = getCommentMessages(channelId, ts)

        comments.forEach {
            if (supportChannelService.isSupportUser(it.user)) {
                deleteMessage(ts)
                return
            }
        }

        val reactions = getReactions(channelId, ts)
        reactions.forEach { reaction ->
            reaction.users.forEach { user ->
                if (supportChannelService.isSupportUser(user)) {
                    deleteMessage(ts)
                    return
                }
            }
        }
    }

    fun updateAllMessages() {
        unansweredMessageRepository.getMessages().forEach {
            updateMessage(it.channelId, it.ts)
        }
    }

    fun addMessage(unansweredMessage: UnansweredMessage) {
        unansweredMessageRepository.addMessage(unansweredMessage)
    }

    fun getAllMessages(): List<UnansweredMessage> {
        return unansweredMessageRepository.getMessages()
    }

    fun deleteMessage(ts: String) {
        unansweredMessageRepository.deleteMessage(ts)
    }

    fun deleteMessagesByChannel(ts: String) {
        unansweredMessageRepository.deleteMessageByChannel(ts)
    }
}