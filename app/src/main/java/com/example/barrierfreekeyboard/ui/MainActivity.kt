package com.example.barrierfreekeyboard.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.barrierfreekeyboard.BuildConfig
import com.example.barrierfreekeyboard.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
        setContentView(R.layout.activity_main)
    }
}