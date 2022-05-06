package com.example.barrierfreekeyboard.ui.settings.page

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.barrierfreekeyboard.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("unused")
class AACKeyboardSettings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.aac_keyboard_pref_screen, rootKey)

        val reset = findPreference<Preference>("none")
        reset?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("WAH")
                setMessage("Tentacult!")
            }
            true
        }
    }
}