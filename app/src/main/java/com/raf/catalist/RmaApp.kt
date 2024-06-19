package com.raf.catalist

import android.app.Application
import androidx.room.Room
import com.raf.catalist.db.AppDatabase

class RmaApp : Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = extracted()
//        database.breedDao().insert(Breed())
    }

    private fun extracted() = Room.databaseBuilder(
            context = this,
            klass = AppDatabase::class.java,
            name = "rma.db"
        ).build()

}
