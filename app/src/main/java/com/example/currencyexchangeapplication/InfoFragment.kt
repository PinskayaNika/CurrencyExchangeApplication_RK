package com.example.currencyexchangeapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val ARG_TITLE = "title"
private const val ARG_FROM = "from"
private const val ARG_TO = "to"
private const val ARG_MIN = "min"
private const val ARG_MAX = "max"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var title: String? = null
    private var from: String? = null
    private var to: String? = null
    private var min: String? = null
    private var max: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
       // super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            title = it.getString(ARG_TITLE)
            from = it.getString(ARG_FROM)
            to = it.getString(ARG_TO)
            min = it.getString(ARG_MIN)
            max = it.getString(ARG_MAX)
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date((title!!.toLong()) * 1000)

        builder.setTitle(sdf.format(netDate).toString())
            .setMessage("from: $from\nto: $to\nmin: $min\nmax: $max")
            .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    Navigation.createNavigateOnClickListener(R.id.action_listFragment_to_infoFragment)
                }
            })
        super.onCreate(savedInstanceState)

        //return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, title: String, from: String, to: String, min: String, max: String) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_TITLE, title)
                    putString(ARG_FROM, from)
                    putString(ARG_TO, to)
                    putString(ARG_MIN, min)
                    putString(ARG_MAX, max)
                }
            }
    }
}