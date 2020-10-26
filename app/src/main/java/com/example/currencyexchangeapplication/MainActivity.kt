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
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.schedulers.Schedulers
import java.util.*
import com.example.currencyexchangeapplication.databinding.ActivityMainBinding
import com.example.currencyexchangeapplication.Data
import com.example.currencyexchangeapplication.Data.Record

import com.example.currencyexchangeapplication.Repository


class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener {

    private val API_KEY = "4f9366a4993b0698da075875200877a598a73c430d397e3304564dd2b2d4186b"
    private lateinit var binding : ActivityMainBinding

    companion object {
        var isSettedLanguage = false
        var isChangedOrientation = false
        var lastOrientation = 0
        internal lateinit var sharedPreferences: SharedPreferences
    }

    private lateinit var ccAdapter: CryptoCurrencyAdapter
    private var numberOfDays: Int? = 5
    private var currency: String? = "RUB"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        //Примеры для тестирования recyclerView
        val bitcoinDayList = arrayListOf(
            CryptoCurrencyByDay(Date(2020, 10, 19), 19.0),
            CryptoCurrencyByDay(Date(2020, 10, 20), 20.0),
            CryptoCurrencyByDay(Date(2020, 10, 21), 21.0),
            CryptoCurrencyByDay(Date(2020, 10, 22), 22.0),
            CryptoCurrencyByDay(Date(2020, 10, 23), 23.0)
        )

        //Примеры для тестирования recyclerView
        val efiriumDayList = arrayListOf(
            CryptoCurrencyByDay(Date(2020, 9, 19), 19.0),
            CryptoCurrencyByDay(Date(2020, 9, 20), 20.0),
            CryptoCurrencyByDay(Date(2020, 9, 21), 21.0),
            CryptoCurrencyByDay(Date(2020, 9, 22), 22.0),
            CryptoCurrencyByDay(Date(2020, 9, 23), 23.0)
        )

        val cryptoCurrencies = arrayListOf(
            CryptoCurrency("Bitcoin", bitcoinDayList),
            CryptoCurrency("Efirium", efiriumDayList)
        )


        val limit = sharedPreferences.getString("limit", "10")?.toInt()


        val cryptoCurrenciesByDayView = findViewById<RecyclerView>(R.id.cryptoCurrenciesByDay)
        cryptoCurrenciesByDayView.setHasFixedSize(false)
        ccAdapter = CryptoCurrencyAdapter(this, cryptoCurrencies, numberOfDays!!)
        cryptoCurrenciesByDayView.adapter = ccAdapter

        setSettings(sharedPreferences)

//        val navController = this.findNavController(R.id.test_nav_fragment)
//        NavigationUI.setupActionBarWithNavController(this, navController)

//        val adapter = CryptoCurrencyAdapter(getRecords(limit = limit?.minus(1)), object : CryptoCurrencyAdapter.Callback {
//            override fun onItemClicked(item: Record) {
//                openDetailedInfo(item)
//            }
//        })
    }

//    override fun onRefresh() {
//        binding.swipeContainer.isRefreshing = true
//        binding.swipeContainer.postDelayed({
//            run() {
//                val fsym = binding.spinnerCrypto.selectedItem.toString()
//                val tsym = binding.spinnerMoney.selectedItem.toString()
//
//                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//                val limit = prefs.getString("limit", "10")?.toInt()
//                val adapter = Adapter(getRecords(limit = limit?.minus(1)), object : Adapter.Callback {
//                    override fun onItemClicked(item: Record) {
//                        openDetailedInfo(item)
//                    }
//                })
//                binding.swipeContainer.isRefreshing = false
//                binding.recyclerview.adapter = adapter
//                binding.infoChosen.text = "Курс за $limit дней пара $fsym/$tsym"
//            }
//        }, 3000)
//    }

    private fun openDetailedInfo(item: Record) {
        val intent = Intent(this, ChildActivity::class.java)
        intent.putExtra("time", item.time)
            .putExtra("open", item.open)
            .putExtra("close", item.close)
            .putExtra("low", item.low)
            .putExtra("high", item.high)
            .putExtra("volumeFrom", item.volumeFrom)
            .putExtra("volumeTo", item.volumeTo)
        startActivity(intent)
    }

    private fun getRecords(limit: Int?): List<Record> {
        val repository = Repository(API_KEY)
        return repository.getHistory(limit = limit)
            .subscribeOn(Schedulers.io())
            .toFlowable()
            .flatMapIterable { answer -> answer.days.records }
            .toList()
            .blockingGet()
            .reversed()
    }

    private fun setSettings(sharedPreferences: SharedPreferences) {
        isChangedOrientation =
            (lastOrientation == 0 && resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) ||
                    (lastOrientation == 1 && resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)

        lastOrientation =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 0

        if (!isSettedLanguage || isChangedOrientation) {
            isSettedLanguage = true
            setLanguageWithReload(sharedPreferences)
        }
        setTextSize(sharedPreferences)
        setTypeface(sharedPreferences)
        setDarkTheme(sharedPreferences)

        setNumberOfDays(sharedPreferences)
        setCurrency(sharedPreferences)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.test_nav_fragment)

        return navController.navigateUp()
    }

    private fun setLanguageWithReload(sharedPreferences: SharedPreferences) {
        setLanguage(sharedPreferences)

        val mainIntent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(mainIntent)

        overridePendingTransition(0, 0)
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
        findViewById<TextView>(R.id.kittenText).setTextColor(color)
    }

    private fun setBackgroundColor(color: Int) {
        findViewById<LinearLayout>(R.id.activity_main).setBackgroundColor(color)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsMenu) {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
        } else
            if (item.itemId == R.id.updaing) {
                //Заново выгружаются данные из интернета
//                run() {
//
//
//                    //val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//                    val limit = sharedPreferences.getString("limit", "10")?.toInt()
//                    val adapter = CryptoCurrencyAdapter(
//                        getRecords(limit = limit?.minus(1)),
//                        object : CryptoCurrencyAdapter.Callback {
//                            override fun onItemClicked(item: Record) {
//                                openDetailedInfo(item)
//                            }
//                        })
//
//                    binding.cryptoCurrenciesByDay?.adapter = adapter
//                }

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
                ccAdapter.notifyDataSetChanged()
            }
            getString(R.string.textSizeKey) -> {
                setTextSize(sharedPreferences)
                ccAdapter.notifyDataSetChanged()
            }
            getString(R.string.russianLanguageKey) -> {
                setLanguageWithReload(sharedPreferences)
            }
            getString(R.string.darkThemeKey) -> {
                setDarkTheme(sharedPreferences)
                ccAdapter.notifyDataSetChanged()
            }
            getString(R.string.numberOfDaysKey) -> {
                setNumberOfDays(sharedPreferences)
                ccAdapter.notifyDataSetChanged()
            }
            getString(R.string.currencySelectKey) -> {
                setCurrency(sharedPreferences)
                ccAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }
}