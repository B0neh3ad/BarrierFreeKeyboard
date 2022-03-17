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

    companion object{
        const val KB_ENG = 0
        const val KB_KOR = 1
        const val KB_SYM = 2
        const val KB_EMO = 3
        const val KB_NUM = 4

        const val KB_AAC = 10

        const val VIB_INT = 70

        const val SPACEBAR = 32

        const val KEYCODE_LF = 10
        const val KEYCODE_DONE = -4
        const val KEYCODE_DELETE = -5
    }
}