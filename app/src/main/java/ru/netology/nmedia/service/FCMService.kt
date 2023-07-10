package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject
import ru.netology.nmedia.R
import ru.netology.nmedia.actions.Action
import ru.netology.nmedia.actions.AddNewPost
import ru.netology.nmedia.actions.Like
import ru.netology.nmedia.auth.AppAuth
import kotlin.math.min
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()
    private val pushStub by lazy {
        getString(R.string.new_notification)
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val authorizeId = AppAuth.getInstance().data.value?.id
        try {
            if (message.data["action"] == null) {
                val pushJson = message.data.values.firstOrNull()?.let { JSONObject(it) }
                val recipientId: String? = pushJson?.optString("recipientId")
                val content: String = pushJson?.optString("content") ?: pushStub
                when (recipientId) {
                    "null", authorizeId.toString() -> handlePush(content)
                    "0" -> AppAuth.getInstance().sendPushToken()
                    else -> AppAuth.getInstance().sendPushToken()
                }
            } else {
                message.data[action]?.let {
                    when (Action.valueOf(it)) {
                        Action.LIKE -> handleLike(
                            gson.fromJson(
                                message.data[content],
                                Like::class.java
                            )
                        )
                        Action.ADDNEWPOST -> handleAddNewPost(
                            gson.fromJson(
                                message.data[content],
                                AddNewPost::class.java
                            )
                        )
                    }
                }
            }
        } catch (e: java.lang.IllegalArgumentException) {
            handleActionNotFound()
        }
    }

    override fun onNewToken(token: String) {
        println(token)
        AppAuth.getInstance().sendPushToken(token)
    }

    private fun handlePush(content: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleAddNewPost(content: AddNewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_add_new_post,
                    content.userName,
                )
            )
            .setContentText(content.content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        content.content
                            .slice(0..min(content.content.length - 1, 300)).plus("...")
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleActionNotFound() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.new_notification,
                )
            )
            .setContentText(
                getString(
                    R.string.notification_update_app,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }
}

