package com.example.recipeapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.R
import com.example.recipeapp.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 milliseconds delay
    }
}
