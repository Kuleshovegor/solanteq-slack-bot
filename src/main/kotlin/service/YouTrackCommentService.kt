package service

import models.YouTrackComment
import org.kodein.di.DI
import org.kodein.di.instance
import repository.YouTrackCommentRepository

class YouTrackCommentService(di: DI) {
    private val youTrackCommentRepository: YouTrackCommentRepository by di.instance()

    fun save(youTrackComment: YouTrackComment) {
        youTrackCommentRepository.save(youTrackComment)
    }
}