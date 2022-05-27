package service

import client.UnexpectedStatusCodeException
import client.YouTrackClient
import models.YouTrackComment
import models.YouTrackMention
import org.kodein.di.DI
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import repository.YouTrackCommentRepository

class YouTrackCommentService(di: DI) {
    private val youTrackCommentRepository: YouTrackCommentRepository by di.instance()
    private val youTrackClient: YouTrackClient by di.instance()
    private val logger = LoggerFactory.getLogger("YouTrackCommentService")

    fun save(youTrackMention: YouTrackMention) {
        youTrackCommentRepository.save(youTrackMention)
    }

    fun getUnansweredCommentsByEmail(email: String): List<YouTrackMention> {
        val comments = youTrackCommentRepository.getByEmail(email)
        val result = mutableListOf<YouTrackMention>()
        comments.forEach {
            try {
                if (youTrackClient.hasCommentBeenAnswered(it)) {
                    deleteMention(it.id)
                } else {
                    result.add(it)
                }
            } catch (e: UnexpectedStatusCodeException) {
                logger.error(e.message)
            }
        }

        return result
    }

    fun deleteMention(id: String) {
        youTrackCommentRepository.deleteOneById(id)
    }

    fun deleteMentions(youTrackComment: YouTrackComment) {
        youTrackCommentRepository.deleteManyByComment(youTrackComment)
    }
}