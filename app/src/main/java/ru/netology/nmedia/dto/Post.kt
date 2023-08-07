package ru.netology.nmedia.dto

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val author: String,
    val authorId: Long = 0L,
    val authorAvatar: String = "",
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Double,
    val shared: Double,
    val views: Double,
    val video: String? = null,
    var attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
) : FeedItem

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem

data class Attachment(
    val url: String,
    val description: String = "",
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE
}

enum class ErrorType {
    LOADING,
    SAVE,
    REMOVE,
    LIKE
}