package com.ust.weatherforecastapp.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.preference.PreferenceFragmentCompat
import com.ust.weatherforecastapp.R
import com.ust.weatherforecastapp.login.LoginFragment

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

}