package kiti.team.fcm_android

import android.app.Notification.FLAG_AUTO_CANCEL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
	
	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		super.onMessageReceived(remoteMessage)
		
		val title = remoteMessage.data["title"] ?: ""
		val body = remoteMessage.data["body"] ?: ""
		val type = remoteMessage.data["type"] ?: ""
		
		val pendingIntent = createPendingIntent(type)
		
		sendNotification(title, body, pendingIntent)
	}
	
	override fun onNewToken(token: String) {
		super.onNewToken(token)
		
		// Send token to your server
	}
	
	private fun createPendingIntent(type: String): PendingIntent? {
		return when (type) {
			"RESULT" -> newResultIntent()
			else -> newResult2Intent()
		}
	}
	
	private fun newResultIntent(): PendingIntent? {
		val resultIntent = Intent(this, ResultActivity::class.java).apply {
			flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
		}
////		// Create the TaskStackBuilder
////		return TaskStackBuilder.create(this).run {
////			// Add the intent, which inflates the back stack
////			addNextIntentWithParentStack(resultIntent)
////			// Get the PendingIntent containing the entire back stack
////			getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
////		}
		
		return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
	}
	
	private fun newResult2Intent(): PendingIntent? {
		val resultIntent = Intent(this, Result2Activity::class.java).apply {
			flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
		}
////		// Create the TaskStackBuilder
////		return TaskStackBuilder.create(this).run {
////			// Add the intent, which inflates the back stack
////			addNextIntentWithParentStack(resultIntent)
////			// Get the PendingIntent containing the entire back stack
////			getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
////		}
		
		return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
	}
	
	private fun sendNotification(title: String, message: String, pendingIntent: PendingIntent?) {
		createNotificationChannel()
		
		val builder = NotificationCompat.Builder(this, CHANNEL_ID)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(title)
			.setContentText(message)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.setContentIntent(pendingIntent)
		
		NotificationManagerCompat.from(this).notify(Random.nextInt(10000), builder.build().apply {
			flags = NotificationCompat.FLAG_AUTO_CANCEL
		})
	}
	
	private fun createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = "CHANEL_NAME"
			val descriptionText = "CHANEL_DESCRIPTION"
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
				description = descriptionText
			}
			// Register the channel with the system
			val notificationManager: NotificationManager =
				getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(channel)
		}
	}
	
	companion object {
		private const val CHANNEL_ID = "kiti.team.fcm_android"
	}
}