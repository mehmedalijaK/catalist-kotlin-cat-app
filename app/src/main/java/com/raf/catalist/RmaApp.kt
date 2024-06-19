package com.raf.catalist

import android.app.Application
import androidx.room.Room
import com.raf.catalist.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp //
class RmaApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}