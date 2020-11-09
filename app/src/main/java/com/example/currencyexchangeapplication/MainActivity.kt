package com.example.currencyexchangeapplication

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import com.example.currencyexchangeapplication.databinding.ActivityMainBinding
import com.example.currencyexchangeapplication.Data.ChildData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.android.synthetic.main.fragment_list.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener {

    private val API_KEY = "4f9366a4993b0698da075875200877a598a73c430d397e3304564dd2b2d4186b"
    private lateinit var binding : ActivityMainBinding

    lateinit var cryptoCompareServiceApi: Service

    companion object {
        var isSettedLanguage = false
        var isChangedOrientation = false
        var lastOrientation = 0
        internal lateinit var sharedPreferences: SharedPreferences
    }

    private lateinit var ccAdapter: CryptoCurrencyAdapter
    var numberOfDays: Int = 5
    var currencyFrom: String = "RUB"
    var currencyTo: String = "USD"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //------------------------------------

        val navController = this.findNavController(R.id.test_nav_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

//        val adapter = CryptoCurrencyAdapter(getRecords(limit = limit?.minus(1)), object : CryptoCurrencyAdapter.Callback {
//            override fun onItemClicked(item: Record) {
//                openDetailedInfo(item)
//            }
//        })
        //------------------------------------

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        PreferenceManager.setDefaultValues(this, R.xml.pref_fragment, false)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey("from_currency")) {
//                currencyFrom = savedInstanceState.getString("from_currency").toString()
//            }
//        }


        //RecyclerView + подключение к нему настроек
//        val cryptoCurrenciesByDayView = findViewById<RecyclerView>(R.id.cryptoCurrenciesByDay)
//        cryptoCurrenciesByDayView.setHasFixedSize(false)
//        ccAdapter = CryptoCurrencyAdapter(this, cryptoCurrencies, numberOfDays!!)
//        cryptoCurrenciesByDayView.adapter = ccAdapter

        numberOfDays = sharedPreferences.getString("days_number", "3")!!.toInt()
        currencyTo = sharedPreferences.getString("currency", "USD")!!

        //setSettings(sharedPreferences)

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val BASE_URL = "https://min-api.cryptocompare.com/"

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        cryptoCompareServiceApi = retrofit.create(Service::class.java)




        setSettings(sharedPreferences)
        //setAdapter(sharedPreferences)

    }

    //------------------------------------

    //функция обновления
    fun onRefresh(fromCurrency: String, toCurrency: String, daysLimit: Int) {
        val call: Call<Data.Answer> = cryptoCompareServiceApi.getHistory(fromCurrency, toCurrency, daysLimit-1)

        call.enqueue(object: Callback<Data.Answer> {
            override fun onFailure(call: Call<Data.Answer>, t: Throwable) {
                Log.d("onFailure", t.message.toString())
            }

            override fun onResponse(call: Call<Data.Answer>, response: Response<Data.Answer>) {
                val exampleList = ArrayList<CryptoCurrencyByDay>()

                val rate: Data.Answer = response.body()!!
                for (data in rate.days!!.childData!!) {
                    exampleList += CryptoCurrencyByDay(data.time!!, data.open!!, fromCurrency, toCurrency, data.low!!, data.high!!)
                }

                cryptoCurrenciesByDay.adapter = CryptoCurrencyAdapter(this@MainActivity, exampleList, numberOfDays)
            ccAdapter = CryptoCurrencyAdapter(this@MainActivity, exampleList, numberOfDays)
            }
        })
    }

//    private fun setAdapter(sharedPreferences: SharedPreferences) {
//        //Примеры для тестирования recyclerView
//        val bitcoinDayList = generateCurrenciesValues(sharedPreferences, 8)
//        //Примеры для тестирования recyclerView
//        val efiriumDayList = generateCurrenciesValues(sharedPreferences, 9)
//
//        //массив списков для разных видов криптовалюты
//        val cryptoCurrencies = arrayListOf(
//            CryptoCurrencyByDay(getString(R.string.bitcoin), bitcoinDayList),
//            CryptoCurrencyByDay(getString(R.string.efirium), efiriumDayList)
//        )
//
//        val cryptoCurrenciesByDayView = findViewById<RecyclerView>(R.id.cryptoCurrenciesByDay)
//        cryptoCurrenciesByDayView.setHasFixedSize(false)
//        ccAdapter = CryptoCurrencyAdapter(this, cryptoCurrencies, numberOfDays!!)
//        cryptoCurrenciesByDayView.adapter = ccAdapter
//    }

//    private fun generateCurrenciesValues(
//        sharedPreferences: SharedPreferences,
//        m: Int
//    ): ArrayList<CryptoCurrencyByDay> {
//        val d: Int = sharedPreferences.getString(
//            getString(R.string.numberOfDaysKey),
//            getString(R.string.defaultNumberOfDaysSetting)
//        )?.toInt()!!
//
//        val a: ArrayList<CryptoCurrencyByDay> = ArrayList()
//        for (i in 1..d) {
//            a.add(CryptoCurrencyByDay(Date(2020, m, 15 + i), 15.0 + i.toDouble()))
//        }
//
//        return a
//    }


    private fun openDetailedInfo(item: ChildData) {
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

//    private fun getRecords(limit: Int?): List<ChildData> {
//        val repository = Repository(API_KEY)
//        return repository.getHistory(limit = limit)
//            .subscribeOn(Schedulers.io())
//            .toFlowable()
//            .flatMapp:layoutManager="LinearLayoutManager"apIterable { answer -> answer.days.childData }
//            .toList()
//            .blockingGet()
//            .reversed()
//    }
    //------------------------------------


    //Настройки
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
        setCurrencyFrom(sharedPreferences)
        setCurrencyTo(sharedPreferences)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.test_nav_fragment)

        return navController.navigateUp()
    }

    //Получение языка при перезагрузке
    private fun setLanguageWithReload(sharedPreferences: SharedPreferences) {
        setLanguage(sharedPreferences)

        val mainIntent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(mainIntent)

        overridePendingTransition(0, 0)
    }

    //Настройка языка
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

    //Настройка размера текста
    private fun setTextSize(sharedPreferences: SharedPreferences) {
        val size: Float = sharedPreferences.getString(
            getString(R.string.textSizeKey),
            getString(R.string.defaultTextSizeSetting)
        )!!.toFloat()

        //findViewById<TextView>(R.id.kittenText).textSize = size
    }

    //Настройка выделения текста (курсив/жирное выделение)
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

        //findViewById<TextView>(R.id.kittenText).setTypeface(null, typeface)
    }

    //Настройка темы (темная/светлая)
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

    //Настройка количества дней
    private fun setNumberOfDays(sharedPreferences: SharedPreferences) {
        val numberOfDays: String? = sharedPreferences.getString(
            getString(R.string.numberOfDaysKey),
            getString(R.string.defaultNumberOfDaysSetting)
        )

        this.numberOfDays = numberOfDays!!.toInt()
    }

    //Настройка валюты, которую переводят
    private fun setCurrencyFrom(sharedPreferences: SharedPreferences) {
        val currency: String? = sharedPreferences.getString(
            getString(R.string.currencyFromSelectKey),
            getString(R.string.defaultNumberOfDaysSetting)
        )

        this.currencyFrom = currency!!
    }
    //Настройка валюты, в которую переводят
    private fun setCurrencyTo(sharedPreferences: SharedPreferences) {
        val currency: String? = sharedPreferences.getString(
            getString(R.string.currencyToSelectKey),
            getString(R.string.defaultNumberOfDaysSetting)
        )

        this.currencyTo = currency!!
    }

    //Настройка цвета текста
    private fun setTextColor(color: Int) {
        //findViewById<TextView>(R.id.kittenText).setTextColor(color)
    }

    //Настройка цвета фона
    private fun setBackgroundColor(color: Int) {
        findViewById<LinearLayout>(R.id.activity_main).setBackgroundColor(color)
    }

    //Создание меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Создание пунктов меню (настройки / обновление)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Настройки
        if (item.itemId == R.id.settingsMenu) {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
            return true
        } else

        //------------------------------------
        //Обновление страницы
            if (item.itemId == R.id.updaing) {
                //Заново выгружаются данные из интернета
                onRefresh(currencyFrom, currencyTo, numberOfDays)
                return true

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
                //------------------------------------

            }

        return super.onOptionsItemSelected(item)
    }

    //Изменение настроек и проверка их корректности
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

    //Применение настроек
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
            getString(R.string.currencyFromSelectKey) -> {
                setCurrencyFrom(sharedPreferences)
                ccAdapter.notifyDataSetChanged()
            }
            getString(R.string.currencyToSelectKey) -> {
                setCurrencyTo(sharedPreferences)
                ccAdapter.notifyDataSetChanged()
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        outState.putString("currency_From", currencyFrom)
//    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = this.findNavController(R.id.test_nav_fragment)
//        return navController.navigateUp()
//    }
}