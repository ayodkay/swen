package com.ayodkay.apps.swen.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.room.Room
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.room.info.AppDatabase
import com.ayodkay.apps.swen.view.main.MainActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Handler().postDelayed({


            val db = Room.databaseBuilder(
                this,
                AppDatabase::class.java, "country"
            ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            if (db.countryDao().getAll() != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, AskLocation::class.java))
                finish()
            }
        }, 3000)
    }
}