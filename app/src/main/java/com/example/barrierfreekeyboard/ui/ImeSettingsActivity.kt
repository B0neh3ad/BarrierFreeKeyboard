package com.example.barrierfreekeyboard.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.doOnLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceHeaderFragmentCompat
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.example.barrierfreekeyboard.BuildConfig
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.databinding.ActivityImeSettingsBinding
import com.example.barrierfreekeyboard.databinding.AppBarBinding
import timber.log.Timber

class ImeSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImeSettingsBinding


    companion object {
        private const val TITLE_TAG = "settingsActivityTitle"
        private lateinit var settingsActivityTitle: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsActivityTitle = getString(R.string.settings_activity)

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        binding = ActivityImeSettingsBinding.inflate(layoutInflater)
        val appbar = AppBarBinding.bind(binding.root)
        setContentView(binding.root)
        setSupportActionBar(appbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = settingsActivityTitle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragments, Settings())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        if ((supportFragmentManager.findFragmentById(R.id.fragments) as Settings)
                .slidingPaneLayout.closePane()) {
            return true
        }
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    class Settings : PreferenceHeaderFragmentCompat() {
        private var title: CharSequence = ""

        override fun onCreatePreferenceHeader(): PreferenceFragmentCompat {
            return DefaultSettings()
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            slidingPaneLayout.addPanelSlideListener(object : SlidingPaneLayout.PanelSlideListener {
                override fun onPanelSlide(panel: View, slideOffset: Float) = Unit
                override fun onPanelOpened(panel: View) = Unit
                override fun onPanelClosed(panel: View) {
                    title = settingsActivityTitle
                    requireActivity().title = title
                }
            })
        }

        override fun onViewStateRestored(savedInstanceState: Bundle?) {
            super.onViewStateRestored(savedInstanceState)
            if (savedInstanceState != null) {
                title = savedInstanceState.getCharSequence(TITLE_TAG) ?: ""
                slidingPaneLayout.doOnLayout {
                    if (slidingPaneLayout.isSlideable) {
                        requireActivity().title = title
                    } else {
                        requireActivity().title = settingsActivityTitle
                    }
                }
            }
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            if (title.isEmpty()) {
                title = settingsActivityTitle
            }
            outState.putCharSequence(TITLE_TAG, title)
        }

        override fun onPreferenceStartFragment(
            caller: PreferenceFragmentCompat,
            pref: Preference
        ): Boolean {
            title = pref.title ?: ""
            if (slidingPaneLayout.isSlideable) {
                requireActivity().title = title
            }
            return super.onPreferenceStartFragment(caller, pref)
        }
    }

    class DefaultSettings : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.keyboard_pref, rootKey)
        }
    }

    @Suppress("unused")
    class SecondarySettings : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.keyboard_pref_s1, rootKey)
        }
    }
}