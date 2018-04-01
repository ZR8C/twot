package com.twot.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import com.twot.MainActivity
import com.twot.R
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class ChangeRefreshDialog() : DialogFragment() {

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_refreshrate, null)

        return AlertDialog.Builder(activity)
                .setTitle("Set refresh rate")
                .setView(dialogView)
                .setNegativeButton("Cancel") { a, _ ->
                    a.cancel()
                }
                .setPositiveButton("Set") { _,_ ->
                    val refreshRate = dialogView.findViewById<EditText>(R.id.refresh_rate).text.toString().toIntOrNull()

                    if (refreshRate == null)
                        dialog.dismiss()
                    else if (refreshRate <= 0)
                        AlertDialog.Builder(activity)
                                .setMessage("Invalid number")
                                .show()
                    else {
                        val asLong = refreshRate.toLong()
                        with(mainActivity.preferences.edit()) {
                            putLong("refresh_rate", asLong)
                            commit()
                        }

                        mainActivity.setRefreshInterval(asLong)
                        dialog.dismiss()
                    }
                }.create()
    }
}