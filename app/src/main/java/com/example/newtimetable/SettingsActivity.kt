package com.example.newtimetable

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false))
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            findPreference<SwitchPreferenceCompat>("dark_theme")?.setOnPreferenceChangeListener { _, newValue ->
                run {
                    if (newValue as Boolean)
                        activity?.setTheme(R.style.AppTheme_Dark)
                    else
                        activity?.setTheme(R.style.AppTheme)
                    activity?.recreate()
                    return@setOnPreferenceChangeListener true
                }
            }
            findPreference<ListPreference>("this_week")?.isVisible =
                findPreference<ListPreference>("number_0f_week")?.value != "1"
            findPreference<ListPreference>("number_0f_week")?.setOnPreferenceChangeListener { _, newValue ->
                run {
                    findPreference<ListPreference>("this_week")?.isVisible = newValue != "1"
                    return@setOnPreferenceChangeListener true
                }
            }
            findPreference<ListPreference>("this_week")
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}