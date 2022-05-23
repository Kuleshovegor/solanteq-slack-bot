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

    fun updateMention(youTrackMention: YouTrackMention) {
//        if (hasCommentBeenAnswered(youTrackMention)) {
//            youTrackCommentRepository.deleteOne(youTrackMention)
//        }
    }

    fun updateAll() {
        youTrackCommentRepository.getAll().forEach {
            updateMention(it)
        }
    }

    fun deleteMentions(youTrackComment: YouTrackComment) {
        youTrackCommentRepository.deleteMany(youTrackComment)
    }
}