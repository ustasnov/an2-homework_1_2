package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Double,
    val shared: Double,
    val views: Double,
    val video: String?,
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val hidden: Boolean = false,
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likedByMe, likes, shared, views, video, attachment?.toDto())

    companion object {
        fun fromDto(dto: Post, hidden: Boolean) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.shared,
                dto.views,
                dto.video,
                AttachmentEmbeddable.fromDto(dto.attachment),
                hidden = hidden
            )
    }
}

data class AttachmentEmbeddable(
    var url: String,
    var description: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, description, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.description , it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(hidden: Boolean = false): List<PostEntity> = map {
    PostEntity.fromDto(it, hidden)
}