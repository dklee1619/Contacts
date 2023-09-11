package com.test.mycontacts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.test.mycontacts.MyItem.Companion.defaultDataList

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val name = defaultDataList.last().aName
        showNotification(context, name)
    }

    private fun showNotification(context: Context, name: String) {
        val channelId = "notification_channel_id"
        val channelName = "Notification Channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingOpenAppIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(context, channelId)
            .setContentTitle("연락처 알림")
            .setContentText("${name}님에게 연락할 시간입니다!")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingOpenAppIntent)
            .setAutoCancel(true)  // 알림을 클릭하면 자동으로 사라지게 한다.
            .build()

        notificationManager.notify(0, notification)

    }
}
