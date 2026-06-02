package com.gulshid.expansetracker


import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpenseTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Global initializations (like Firebase or logs) can go here later
    }
}