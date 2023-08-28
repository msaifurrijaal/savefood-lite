package com.msaifurrijaal.savefood.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.ui.chat.ChatActivity
import com.msaifurrijaal.savefood.ui.location.LocationActivity
import com.msaifurrijaal.savefood.ui.main.MainActivity

class Splashctivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashctivity)

        delayAndGoToLogin()

    }

    private fun delayAndGoToLogin() {
        Handler(Looper.getMainLooper())
            .postDelayed({
                startActivity(Intent(this, LocationActivity::class.java))
                finish()
            }, 1200)
    }

}