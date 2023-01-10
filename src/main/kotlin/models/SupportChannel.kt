package models

data class SupportChannel(val teamId: String, val id: String, val name: String, val supportUserIds: Set<String>)