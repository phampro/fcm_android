package kiti.team.fcm_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class ResultActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_result)
	}
	
	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		Log.d("ResultActivity", "onNewIntent")
		//TODO Handle if have data
	}
	
	override fun onBackPressed() {
		if (isTaskRoot) {
			val intent = Intent(this, MainActivity::class.java).apply {
				action = Intent.ACTION_MAIN
				addCategory(Intent.CATEGORY_LAUNCHER)
			}
			startActivity(intent)
			finish()
		} else {
			super.onBackPressed()
		}
	}
}