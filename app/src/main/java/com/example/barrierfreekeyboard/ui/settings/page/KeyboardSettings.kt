package com.example.barrierfreekeyboard.ui.settings.page

import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.barrierfreekeyboard.R
import com.example.barrierfreekeyboard.ui.KeyboardConstants
import com.example.barrierfreekeyboard.ui.PrefKeys

@Suppress("unused")
class KeyboardSettings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.keyboard_pref_screen, rootKey)
        val reset = findPreference<Preference>("reset_settings")
        reset?.setOnPreferenceClickListener {
            val preference = PreferenceManager.getDefaultSharedPreferences(requireContext())
            preference.edit {
                putBoolean(PrefKeys.KB_USE_NUM_PAD, KeyboardConstants.KB_DEFAULT_USE_NUMPAD)
                putInt(PrefKeys.KB_PORTRAIT_HEIGHT, KeyboardConstants.KB_DEFAULT_PORTRAIT_HEIGHT)
                putInt(PrefKeys.KB_LANDSCAPE_HEIGHT, KeyboardConstants.KB_DEFAULT_LANDSCAPE_HEIGHT)
                putInt(PrefKeys.KB_SOUND, KeyboardConstants.KB_DEFAULT_SOUND)
                putInt(PrefKeys.KB_VIBRATE, KeyboardConstants.KB_DEFAULT_VIBRATE)
                putInt(PrefKeys.KB_INITIAL_INTERVAL, KeyboardConstants.KB_DEFAULT_INITIAL_INTERVAL)
                putInt(PrefKeys.KB_NORMAL_INTERVAL, KeyboardConstants.KB_DEFAULT_NORMAL_INTERVAL)
            }
            onCreatePreferences(savedInstanceState, rootKey)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPaddingRelative(0, 0, 0, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }
    }
}