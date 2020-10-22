package com.example.currencyexchangeapplication

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import java.util.*

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener {
    companion object {
        var isSettedLanguage = false
    }

    private var numberOfDays: Int? = 7
    private var currency: String? = "RUB"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        setSettings(sharedPreferences)
    }

    private fun setSettings(sharedPreferences: SharedPreferences) {
        if (!isSettedLanguage) {
            isSettedLanguage = true
            setLanguage(sharedPreferences)
        }
        setTextSize(sharedPreferences)
        setTypeface(sharedPreferences)
        setDarkTheme(sharedPreferences)

        setNumberOfDays(sharedPreferences)
        setCurrency(sharedPreferences)
    }

    private fun setLanguage(sharedPreferences: SharedPreferences) {
        val isRussianLanguage = sharedPreferences.getBoolean(
            getString(R.string.russianLanguageKey),
            resources.getBoolean(R.bool.defaultRussianLanguage)
        )

        val locale: Locale = if (isRussianLanguage)
            Locale.forLanguageTag("ru-RU")
        else
            Locale.forLanguageTag("en-US")

        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, null)

        val mainIntent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(mainIntent)

        overridePendingTransition(0, 0)
    }

    private fun setTextSize(sharedPreferences: SharedPreferences) {
        val size: Float = sharedPreferences.getString(
            getString(R.string.textSizeKey),
            getString(R.string.defaultTextSizeSetting)
        )!!.toFloat()

        findViewById<TextView>(R.id.kittenText).textSize = size
    }

    private fun setTypeface(sharedPreferences: SharedPreferences) {
        val isItalic = sharedPreferences.getBoolean(
            getString(R.string.italicsTextKey),
            resources.getBoolean(R.bool.defaultItalicsText)
        )
        val isBold = sharedPreferences.getBoolean(
            getString(R.string.boldTextKey),
            resources.getBoolean(R.bool.defaultBoldText)
        )

        val typeface = if (isItalic) {
            if (isBold)
                Typeface.BOLD_ITALIC
            else
                Typeface.ITALIC
        } else {
            if (isBold)
                Typeface.BOLD
            else
                Typeface.NORMAL
        }

        findViewById<TextView>(R.id.kittenText).setTypeface(null, typeface)
    }

    private fun setDarkTheme(sharedPreferences: SharedPreferences) {
        val isDarkTheme = sharedPreferences.getBoolean(
            getString(R.string.darkThemeKey),
            resources.getBoolean(R.bool.defaultDarkTheme)
        )

        if (isDarkTheme) {
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.BLACK)
        } else {
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.WHITE)
        }
    }

    private fun setNumberOfDays(sharedPreferences: SharedPreferences) {
        val numberOfDays: String? = sharedPreferences.getString(
            getString(R.string.numberOfDaysKey),
            getString(R.string.defaultNumberOfDaysSetting)
        )

        this.numberOfDays = numberOfDays?.toInt()
    }

    private fun setCurrency(sharedPreferences: SharedPreferences) {
        val currency: String? = sharedPreferences.getString(
            getString(R.string.currencySelectKey),
            getString(R.string.defaultNumberOfDaysSetting)
        )

        this.currency = currency
    }

    private fun setTextColor(color: Int) {
        val textView = findViewById<TextView>(R.id.kittenText)

        textView.setTextColor(color)
    }

    private fun setBackgroundColor(color: Int) {
        val mainActivityScreen = findViewById<LinearLayout>(R.id.activity_main)

        mainActivityScreen.setBackgroundColor(color)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsMenu) {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        try {
            when (preference.key) {
                getString(R.string.textSizeKey) -> {
                    val textSize: Int = (newValue as String).toInt()
                    if (textSize < 1) {
                        Toast.makeText(this, "Size cant be less than one", Toast.LENGTH_LONG)
                            .show()
                        return false
                    }
                    return true
                }
                getString(R.string.numberOfDaysKey) -> {
                    val numberOfDays: Int = (newValue as String).toInt()
                    if (numberOfDays < 1) {
                        Toast.makeText(
                            this, "Number of days cant be less than one", Toast.LENGTH_LONG
                        ).show()
                        return false
                    }
                    return true
                }
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid number, enter integer!", Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        when (s) {
            getString(R.string.italicsTextKey), getString(R.string.boldTextKey) -> {
                setTypeface(sharedPreferences)
            }
            getString(R.string.textSizeKey) -> {
                setTextSize(sharedPreferences)
            }
            getString(R.string.russianLanguageKey) -> {
                setLanguage(sharedPreferences)
            }
            getString(R.string.darkThemeKey) -> {
                setDarkTheme(sharedPreferences)
            }
            getString(R.string.numberOfDaysKey) -> {
                setNumberOfDays(sharedPreferences)
            }
            getString(R.string.currencySelectKey) -> {
                setCurrency(sharedPreferences)
            }
        }
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }
}