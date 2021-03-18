package kiti.team.fcm_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
	
	private val TAG = "FCMService"
	
	private val broadcaster by lazy {
		LocalBroadcastManager.getInstance(this)
	}
	
	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		super.onMessageReceived(remoteMessage)
		
		val title = remoteMessage.data["title"] ?: ""
		val body = remoteMessage.data["body"] ?: ""
		val type = remoteMessage.data["type"] ?: ""
		
		val pendingIntent = createPendingIntent(type)
		
		sendNotification(title, body, pendingIntent)
		
		val intent = Intent("FCM_MESSAGE")
		intent.putExtra("title", title)
		intent.putExtra("body", body)
		intent.putExtra(type, type)
		broadcaster.sendBroadcast(intent)
	}
	
	override fun onNewToken(token: String) {
		super.onNewToken(token)
		
		// Send token to your server
	}
	
	private fun createPendingIntent(type: String): PendingIntent? {
		return when (type) {
			"RESULT" -> newResultIntent()
			"RESULT2" -> newResult2Intent()
			else -> null
		}
	}
	
	private fun newResultIntent(): PendingIntent? {
		val resultIntent = Intent(this, ResultActivity::class.java)
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
		
		val builder = NotificationCompat.Builder(this, CHANNEL_ID)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(title)
			.setContentText(message)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.setContentIntent(pendingIntent)
		
		createNotificationChannel()
		
		val notification = builder.build()
		
		NotificationManagerCompat.from(this).notify(Random.nextInt(1000), notification)
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
	
	fun showNotification(context: Context, cls: Class<*>, title: String, content: String) {
		val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		
		val notificationIntent = Intent(context, cls)
		notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
		
		val pendingIntent = PendingIntent.getActivity(
			this,
			0,
			notificationIntent,
			PendingIntent.FLAG_UPDATE_CURRENT
		)

//		val stackBuilder = TaskStackBuilder.create(context)
//		stackBuilder.addParentStack(cls)
//		stackBuilder.addNextIntent(notificationIntent)
//
//		val pendingIntent = stackBuilder.getPendingIntent(
//			DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT
//		)
		
		val notificationManager: NotificationManager? =
			context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				DAILY_REMINDER_CHANEL_ID,
				"Channel human readable title",
				NotificationManager.IMPORTANCE_DEFAULT
			)
			notificationManager?.createNotificationChannel(channel)
		}
		
		val builder = NotificationCompat.Builder(context, DAILY_REMINDER_CHANEL_ID)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(title)
			.setContentText(content)
			.setSound(alarmSound)
			.setContentIntent(pendingIntent)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
		
		with(NotificationManagerCompat.from(context)) {
			notify(DAILY_REMINDER_NOTIFY_ID, builder.build())
		}
	}
	
	companion object {
		private const val CHANNEL_ID = "fcm_android"
		
		const val DAILY_REMINDER_REQUEST_CODE = 1
		const val DAILY_REMINDER_CHANEL_ID = "12345"
		const val DAILY_REMINDER_NOTIFY_ID = 9999
	}
}