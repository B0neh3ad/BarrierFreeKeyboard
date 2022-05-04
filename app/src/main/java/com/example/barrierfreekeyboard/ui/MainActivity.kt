package com.example.barrierfreekeyboard.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.barrierfreekeyboard.BuildConfig
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.databinding.ActivityMainBinding
import com.example.barrierfreekeyboard.databinding.AppBarBinding
import com.example.barrierfreekeyboard.databinding.MainKeyboardSettingCardBinding
import com.example.barrierfreekeyboard.ui.settings.ImeSettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_setting -> {
                startActivity(Intent(this, ImeSettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private var firstSplash: Boolean = false
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun applySplash() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (firstSplash) {
                        // firstSplash 가 true 가 되면 splash 가 넘어가짐.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        if (!firstSplash) {
            CoroutineScope(Dispatchers.Default).launch {
                delay(1500)
                firstSplash = true
            }
        }

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val self = ScaleAnimation.RELATIVE_TO_SELF
            val alphaAnim = AlphaAnimation(1F, 0F).defaultSetting()
            val scaleAnim = ScaleAnimation(
                1F, 1.5F,
                1F, 1.5F,
                self, 0.5F,
                self, 0.5F
            ).defaultSetting()
            val anim = AnimationSet(false).apply {
                addAnimation(alphaAnim)
                addAnimation(scaleAnim)
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        splashScreenView.remove()
                    }
                })
            }
            splashScreenView.startAnimation(anim)
        }
    }

    private fun Animation.defaultSetting(): Animation {
        fillAfter = true
        fillBefore = true
        isFillEnabled = true
        duration = resources.getInteger(R.integer.material_motion_duration_medium_2).toLong()
        return this
    }
}