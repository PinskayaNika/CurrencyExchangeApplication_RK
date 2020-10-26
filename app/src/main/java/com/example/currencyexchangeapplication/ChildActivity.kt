package com.example.currencyexchangeapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.currencyexchangeapplication.databinding.ActivityChildBinding
import java.sql.Date
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ChildActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChildBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_child)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_child)

        val time = intent.getLongExtra("time", 0)

        val dateValue =
            SimpleDateFormat("dd-MM-yyyy").format(SimpleDateFormat("yyyy-MM-dd").parse(Date(time * 1000).toString()))
        val openFormatted = DecimalFormat("#0.00").format(getFloatExtra("open")).toString()
        val closeFormatted = DecimalFormat("#0.00").format(getFloatExtra("close")).toString()
        val maxFormatted = DecimalFormat("#0.00").format(getFloatExtra("high")).toString()
        val minFormatted = DecimalFormat("#0.00").format(getFloatExtra("low")).toString()
        val volumeFromFormatted =
            DecimalFormat("#0.00").format(getFloatExtra("volumeFrom")).toString()
        val volumeToFormatted = DecimalFormat("#0.00").format(getFloatExtra("volumeTo")).toString()

        binding.textDate.text = "date: $dateValue"
        binding.textOpen.text = "open: $openFormatted"
        binding.textClose.text = "close: $closeFormatted"
        binding.textMin.text = "min: $minFormatted"
        binding.textMax.text = "max: $maxFormatted"
        binding.textVolumeFrom.text = "volumeFrom: $volumeFromFormatted"
        binding.textVolumeTo.text = "volumeTo: $volumeToFormatted"
    }

    private fun getFloatExtra(key: String): Float {
        val parent = intent
        if (parent.hasExtra(key)) return parent.getFloatExtra(key, 0F)
        return 0F
    }
}
