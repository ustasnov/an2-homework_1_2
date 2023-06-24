package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Double,
    val shared: Double,
    val views: Double,
    val video: String? = null,
    var attachment: Attachment? = null,
)

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