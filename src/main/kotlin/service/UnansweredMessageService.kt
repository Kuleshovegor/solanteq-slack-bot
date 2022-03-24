package service

import models.UnansweredMessage
import repository.UnansweredMessageRepository

class UnansweredMessageService(
    private val unansweredMessageRepository: UnansweredMessageRepository
) {

    fun addMessage(unansweredMessage: UnansweredMessage) {
        unansweredMessageRepository.addMessage(unansweredMessage)
    }

    fun getAllMessages(): List<UnansweredMessage> {
        return unansweredMessageRepository.getMessages()
    }

    fun deleteMessage(ts: String) {
        unansweredMessageRepository.deleteMessage(ts)
    }
}