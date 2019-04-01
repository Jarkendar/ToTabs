package com.jarkendar.totabs.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jarkendar.totabs.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val intent = Intent(applicationContext, MainActivity::class.java)
        waitAndRun(intent)
    }

    private fun waitAndRun(intent: Intent) {
        val thread = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(TIME_DELAYED)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(intent)
                    finish()
                }
            }
        }
        thread.start()
    }

    companion object {

        private const val TIME_DELAYED: Long = 3000
    }
}
