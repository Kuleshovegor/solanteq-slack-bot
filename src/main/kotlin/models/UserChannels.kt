package models

import com.slack.api.model.Conversation

data class UserChannels(val id: String, val channels: List<Conversation>)