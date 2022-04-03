package service

import models.YouTrackComment
import models.YouTrackMention
import org.kodein.di.DI
import org.kodein.di.instance
import repository.YouTrackCommentRepository

class YouTrackCommentService(di: DI) {
    private val youTrackCommentRepository: YouTrackCommentRepository by di.instance()

    fun save(youTrackMention: YouTrackMention) {
        youTrackCommentRepository.save(youTrackMention)
    }

    fun deleteMentions(youTrackComment: YouTrackComment) {
        youTrackCommentRepository.delete(youTrackComment)
    }
}