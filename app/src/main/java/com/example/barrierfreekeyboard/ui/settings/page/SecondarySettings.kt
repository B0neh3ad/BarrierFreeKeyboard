package com.example.barrierfreekeyboard.ui.settings.page

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.barrierfreekeyboard.R

@Suppress("unused")
class SecondarySettings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.keyboard_pref_s1, rootKey)
    }
}