package com.shikidroid

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shikidroid.appconstants.NetworkConstants
import com.shikidroid.domain.interactors.UserInteractor
import com.shikidroid.domain.interactors.UserLocalInteractor
import com.shikidroid.domain.models.user.UserUnreadMessagesCountModel
import com.shikidroid.utils.notificationManager
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

/**
 * Класс фоновой работы для получения информации о количестве новых сообщений и уведомлении о них
 */
internal class NotificationWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val userInteractor: UserInteractor,
    private val profileStorageInteractor: UserLocalInteractor,
) : Worker(context, params) {

    override fun doWork(): Result {
        loadMessages()
        return Result.success()
    }

    private val compositeDisposable = CompositeDisposable()

    private fun loadMessages() {
        val id = profileStorageInteractor.getUserId()
        if (id != NetworkConstants.NO_ID) {
            compositeDisposable.add(
                userInteractor.getUnreadMessages(id = id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.messages > 0 || it.notifications > 0 || it.news > 0) {
                            showNotification(
                                context = applicationContext,
                                messages = it
                            )
                        }
                    }, {

                    })
            )
        }
    }

    private fun showNotification(context: Context, messages: UserUnreadMessagesCountModel) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_MESSAGE_NOTIFICATION_ID)
            .setSmallIcon(R.drawable.ic_shikimori)
            .setContentTitle("У вас есть новые сообщения")
            .setContentText(
                "новых сообщений: ${messages.messages}" + "\n" +
                        "новостей: ${messages.news}" + "\n" +
                        "уведомлений: ${messages.notifications}"
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.notificationManager()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_MESSAGE_NOTIFICATION_ID,
                CHANNEL_MESSAGE_NOTIFICATION_ID,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(CHANNEL_MESSAGE_NOTIFICATION_ID, NOTIFICATION_ID, notificationBuilder)
        } else {
            notificationManager.notify(CHANNEL_MESSAGE_NOTIFICATION_ID, NOTIFICATION_ID, notificationBuilder)
        }
    }

    companion object {

        const val NOTIFICATION_ID = 777
        const val CHANNEL_MESSAGE_NOTIFICATION_ID = "CHANNEL_MESSAGE_NOTIFICATION_ID"
    }
}