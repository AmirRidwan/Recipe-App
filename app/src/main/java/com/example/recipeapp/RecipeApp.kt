package com.example.recipeapp

import android.app.Application
import com.google.firebase.FirebaseApp

class RecipeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
