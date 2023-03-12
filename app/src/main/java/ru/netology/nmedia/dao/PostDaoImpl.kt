package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    val postAuthor = "Stanislav Ugarov"

    companion object {
        val DDL = """
            CREATE TABLE ${PostColumns.TABLE} (
                ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
                ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
                ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
                ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_SHARED} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_VIEWS} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_VIDEO} TEXT NOT NULL
            );
            """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKED_BY_ME = "likeByMe"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_SHARED = "shared"
        const val COLUMN_VIEWS = "views"
        const val COLUMN_VIDEO = "video"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKED_BY_ME,
            COLUMN_LIKES,
            COLUMN_SHARED,
            COLUMN_VIEWS,
            COLUMN_VIDEO
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID}"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            put(PostColumns.COLUMN_AUTHOR, postAuthor)
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(
                PostColumns.COLUMN_PUBLISHED,
                SimpleDateFormat("dd MMMM в HH:mm").format(Calendar.getInstance().time)
            )
            put(PostColumns.COLUMN_LIKED_BY_ME, post.likedByMe)
            put(PostColumns.COLUMN_LIKES, post.likes)
            put(PostColumns.COLUMN_SHARED, post.shared)
            put(PostColumns.COLUMN_VIEWS, post.views)
            put(PostColumns.COLUMN_VIDEO, post.video)
        }
        val id = if (post.id != 0L) {
            db.update(
                PostColumns.TABLE,
                values,
                "${PostColumns.COLUMN_ID} = ?",
                arrayOf(post.id.toString())
            )
            post.id
        } else {
            db.insert(PostColumns.TABLE, null, values)
        }
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
              likes = likes + CASE WHEN likeByMe THEN -1 ELSE 1 END,
              likeByMe = CASE WHEN likeByMe THEN 0 ELSE 1 END
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
              shared = shared + 1
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }

    private fun map(cursor: Cursor): Post {
        return with(cursor) {
            Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                likes = getDouble(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                shared = getDouble(getColumnIndexOrThrow(PostColumns.COLUMN_SHARED)),
                views = getDouble(getColumnIndexOrThrow(PostColumns.COLUMN_VIEWS)),
                video = getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO))
            )
        }
    }
}
