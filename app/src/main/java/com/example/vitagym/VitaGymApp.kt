package com.example.vitagym

import android.app.Application
import com.google.firebase.FirebaseApp

class VitaGymApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
