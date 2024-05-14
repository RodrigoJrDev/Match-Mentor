package com.example.matchmentor.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.matchmentor.R
import java.util.Timer
import java.util.TimerTask

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        progressBar = findViewById(R.id.progressBar)

        supportActionBar?.hide()

        simulateProgress()
    }

    private fun simulateProgress() {
        val totalTime = 3000
        var progressTime = 0
        val timer = Timer()
        val progressTask = object : TimerTask() {
            override fun run() {
                progressTime += 100
                progressBar.progress = (progressTime * 100 / totalTime)

                if (progressTime >= totalTime) {
                    timer.cancel()
                    runOnUiThread {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
        timer.schedule(progressTask, 0, 100)
    }
}
