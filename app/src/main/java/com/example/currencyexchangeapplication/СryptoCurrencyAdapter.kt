package com.example.currencyexchangeapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CryptoCurrencyAdapter(
    private val cryptoCurrenciesByDay: ArrayList<CryptoCurrency>,
    private val nDays: Int
) : RecyclerView.Adapter<CryptoCurrencyAdapter.ViewHolder>() {
    private val NAME_TYPE = 0
    private val DAY_TYPE = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CryptoCurrencyAdapter.ViewHolder {
        val view: View = when (viewType) {
            NAME_TYPE -> LayoutInflater.from(parent.context)
                .inflate(R.layout.crypto_currency_name_holder, parent, false);
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.crypto_currency_day_holder, parent, false);
        }

        MainActivity
        return CryptoCurrencyAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CryptoCurrencyAdapter.ViewHolder,
        position: Int
    ) {

        when (getItemViewType(position)) {
            NAME_TYPE -> holder.name?.text =
                cryptoCurrenciesByDay[position / (nDays + 1)].cryptoCurrencyName
            else -> {
                val date: Date =
                    cryptoCurrenciesByDay[position / (nDays + 1)].cryptoCurrencyByDays[(position % (nDays + 1)) - 1].date
                holder.date?.text =
                    date.date.toString() + "." + date.month.toString() + "." + date.year.toString()
                holder.value?.text =
                    cryptoCurrenciesByDay[position / (nDays + 1)].cryptoCurrencyByDays[(position % (nDays + 1)) - 1].value.toString()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position % (nDays + 1) == 0)
            return NAME_TYPE
        return DAY_TYPE
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