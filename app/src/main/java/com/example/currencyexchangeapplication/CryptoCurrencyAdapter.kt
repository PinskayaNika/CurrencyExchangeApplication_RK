package com.example.currencyexchangeapplication

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CryptoCurrencyAdapter(
    private val context: Context,
    private val cryptoCurrenciesByDay: ArrayList<CryptoCurrency>,
    private val nDays: Int
) : RecyclerView.Adapter<CryptoCurrencyAdapter.ViewHolder>() {
    private val cryptoNameType = 0
    private val cryptoItemType = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = when (viewType) {
            cryptoNameType -> LayoutInflater.from(parent.context)
                .inflate(R.layout.crypto_currency_name_holder, parent, false)
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.crypto_currency_day_holder, parent, false)
        }

        MainActivity
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        when (getItemViewType(position)) {
            cryptoNameType -> holder.name?.text =
                cryptoCurrenciesByDay[position / (nDays + 1)].cryptoCurrencyName
            else -> {
                val sb: StringBuilder = StringBuilder()
                val date: Date =
                    cryptoCurrenciesByDay[position / (nDays + 1)].cryptoCurrencyByDays[(position % (nDays + 1)) - 1].date

                val day = if (date.date < 10)
                    "0" + date.date.toString()
                else
                    date.date.toString()

                val month = if (date.month < 10)
                    "0" + date.month.toString()
                else
                    date.month.toString()

                holder.date?.text = sb.append(day).append(".").append(month).append(".")
                    .append(date.year.toString()).toString()
                holder.value?.text =
                    cryptoCurrenciesByDay[position / (nDays + 1)].cryptoCurrencyByDays[(position % (nDays + 1)) - 1].value.toString()
            }
        }

        setTextSize(holder)
        setTypeface(holder)
        setDarkTheme(holder)
    }

    private fun setTextSize(holder: ViewHolder) {
        val size: Float = MainActivity.sharedPreferences.getString(
            context.getString(R.string.textSizeKey),
            context.getString(R.string.defaultTextSizeSetting)
        )!!.toFloat()
        holder.name?.textSize = size + 5.0f
        holder.date?.textSize = size
        holder.value?.textSize = size
    }

    private fun setTypeface(holder: ViewHolder) {
        val isItalic = MainActivity.sharedPreferences.getBoolean(
            context.getString(R.string.italicsTextKey),
            context.resources.getBoolean(R.bool.defaultItalicsText)
        )
        val isBold = MainActivity.sharedPreferences.getBoolean(
            context.getString(R.string.boldTextKey),
            context.resources.getBoolean(R.bool.defaultBoldText)
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

        holder.name?.setTypeface(null, typeface)
        holder.date?.setTypeface(null, typeface)
        holder.value?.setTypeface(null, typeface)
    }

    private fun setDarkTheme(holder: ViewHolder) {
        val isDarkTheme = MainActivity.sharedPreferences.getBoolean(
            context.getString(R.string.darkThemeKey),
            context.resources.getBoolean(R.bool.defaultDarkTheme)
        )

        if (isDarkTheme) {
            setTextColor(holder, Color.WHITE)
            setBackgroundColor(holder, Color.BLACK)
        } else {
            setTextColor(holder, Color.BLACK)
            setBackgroundColor(holder, Color.WHITE)
        }
    }

    private fun setTextColor(holder: ViewHolder, color: Int) {
        holder.name?.setTextColor(color)
        holder.date?.setTextColor(color)
        holder.value?.setTextColor(color)
    }

    private fun setBackgroundColor(holder: ViewHolder, color: Int) {
        holder.name?.setBackgroundColor(color)
        holder.date?.setBackgroundColor(color)
        holder.value?.setBackgroundColor(color)
    }

    override fun getItemViewType(position: Int): Int {
        if (position % (nDays + 1) == 0)
            return cryptoNameType
        return cryptoItemType
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView? = view.findViewById(R.id.cryptoCurrencyName)
        val date: TextView? = view.findViewById(R.id.cryptoCurrencyDate)
        val value: TextView? = view.findViewById(R.id.cryptoCurrencyValue)
    }

    override fun getItemCount(): Int {
        return (nDays + 1) * cryptoCurrenciesByDay.size
    }
}