package jw.adamiak.hangout.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import jw.adamiak.hangout.R
import jw.adamiak.hangout.ui.main_screen.MainMenuActivity


val CHANNEL_ID = "hangout_channel"
val CHANNEL_NAME = "HangOut"
val NOTIFICATION_RADIUS = 300
private val NOTIFICATION_ID = 0


fun NotificationManager.sendNotification(message: String, context: Context) {
	val contentIntent = Intent(context, MainMenuActivity::class.java)

	val contentPendingIntent = PendingIntent.getActivity(
		context,
		NOTIFICATION_ID,
		contentIntent,
		PendingIntent.FLAG_UPDATE_CURRENT
	)

	val cloudIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_cloud)

	val notificationsBuilder = NotificationCompat.Builder(
		context, CHANNEL_ID
	)
		.setSmallIcon(R.drawable.ic_cloud)
		.setContentTitle(CHANNEL_NAME)
		.setContentText(message)
		.setContentIntent(contentPendingIntent)
		.setAutoCancel(true)
		.setPriority(NotificationCompat.PRIORITY_HIGH)

	notify(NOTIFICATION_ID, notificationsBuilder.build())
}

fun NotificationManager.cancelAllNotifications(){
	cancelAll()
}