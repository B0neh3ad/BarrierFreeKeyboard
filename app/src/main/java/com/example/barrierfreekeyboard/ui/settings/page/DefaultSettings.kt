package com.example.barrierfreekeyboard.ui.settings.page

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.barrierfreekeyboard.R

class DefaultSettings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_pref_screen, rootKey)
    }
}