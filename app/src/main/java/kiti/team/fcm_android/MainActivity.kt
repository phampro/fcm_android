package kiti.team.fcm_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
	private val TAG = MainActivity::class.java.simpleName
	
	private val TEXT_STATE = "GAME_STATE_KEY"
	
	private lateinit var fcmReceiver: BroadcastReceiver
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		val text = savedInstanceState?.getString(TEXT_STATE) ?: "Welcome!"
		
		val textView = findViewById<TextView>(R.id.textView)
		textView.text = text
		
		fcmReceiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				textView.text = intent?.getStringExtra("title")
			}
		}
		
		LocalBroadcastManager
			.getInstance(this)
			.registerReceiver(fcmReceiver, IntentFilter("FCM_MESSAGE"))
		
		FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
			if (!task.isSuccessful) {
				Log.w(TAG, "Fetching FCM registration token failed", task.exception)
				return@addOnCompleteListener
			}
			
			// Get new FCM registration token
			val token = task.result
			
			// Log
			Log.d(TAG, "[FirebaseToken][$token]")
		}
		
		val fcmType = intent.getStringExtra("type")
		if (fcmType == "RESULT") {
			startActivity(Intent(this, ResultActivity::class.java))
		} else if (fcmType == "RESULT2") {
			startActivity(Intent(this, Result2Activity::class.java))
		}
		
		
		val progressBar = findViewById<ProgressBar>(R.id.progressBar)
		Handler().postDelayed({
			runOnUiThread {
				progressBar.visibility = View.GONE
			}
		}, 5000)
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		
		outState.run {
			putString(TEXT_STATE, "Welcome back!")
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		
		LocalBroadcastManager
			.getInstance(this)
			.unregisterReceiver(fcmReceiver)
	}
}