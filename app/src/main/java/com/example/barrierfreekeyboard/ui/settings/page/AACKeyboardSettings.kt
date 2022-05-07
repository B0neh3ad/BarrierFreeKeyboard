package com.example.barrierfreekeyboard.ui.settings.page

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.barrierfreekeyboard.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("unused")
class AACKeyboardSettings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.aac_keyboard_pref_screen, rootKey)

        val reset = findPreference<Preference>("wah")
        reset?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("WAH")
                setMessage("Tentacult!")
                setPositiveButton(android.R.string.ok, null)
            }.show()
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