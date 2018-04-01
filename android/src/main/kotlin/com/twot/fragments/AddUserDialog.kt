package com.twot.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.twot.MainActivity
import com.twot.R
import com.twot.core.models.TweetSource
import com.twot.core.models.TweetSourceEntity
import com.twot.core.models.TweetSourceType
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import kotlinx.android.synthetic.main.dialog_adduser.*
import java.util.*

//todo remove repeating code (merge add user / add term)
class AddUserDialog() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val customView = activity.layoutInflater.inflate(R.layout.dialog_adduser, null)
        val builder = AlertDialog.Builder(activity)
                .setTitle("Add user")
                .setView(customView)
                .setNegativeButton("Cancel") { a, _ ->
                    a.cancel()
                }
                .setPositiveButton("Add") { _, _ ->
                    val userName = customView.findViewById<EditText>(R.id.userTxt).text.toString()

                    if (userName.isBlank())
                        dialog.dismiss()

                    val isDuplicate = (data count (TweetSource::class) where (TweetSource::title eq userName) and (TweetSource::type eq TweetSourceType.ACCOUNT)).get().call() > 0

                    if (!isDuplicate) {
                        val tweetSource = TweetSourceEntity().apply {
                            title = userName
                            type = TweetSourceType.ACCOUNT
                            enabled = true
                        }

                        println("Inserting new tweet source $userName")
                        data.insert(tweetSource).blockingGet()

                        dialog.dismiss()
                    } else {
                        println("Tweet source is duplicate")
                        AlertDialog.Builder(activity)
                                .setMessage("User already exists")
                                .show()
                    }
                }

        return builder.create()
    }

    private lateinit var data: KotlinReactiveEntityStore<Persistable>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        data = (context as MainActivity).data
    }
}

class AddTermDialog() : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val customView = activity.layoutInflater.inflate(R.layout.dialog_adduser, null)
        return AlertDialog.Builder(activity)
                .setTitle("Add search term")
                .setView(customView)
                .setNegativeButton("Cancel") { a, _ ->
                    a.cancel()
                }
                .setPositiveButton("Add") { _, _ ->
                    val userName = customView.findViewById<EditText>(R.id.userTxt).text.toString()

                    if (userName.isBlank())
                        dialog.dismiss()

                    val isDuplicate = (data count (TweetSource::class) where (TweetSource::title eq userName) and (TweetSource::type eq TweetSourceType.SEARCH_TERM)).get().call() > 0

                    if (!isDuplicate) {
                        val tweetSource = TweetSourceEntity().apply {
                            title = userName
                            type = TweetSourceType.SEARCH_TERM
                            enabled = true
                        }

                        println("Inserting new tweet source $userName")
                        data.insert(tweetSource).blockingGet()

                        dialog.dismiss()
                    } else {
                        println("Tweet source is duplicate")
                        AlertDialog.Builder(activity)
                                .setMessage("Term already exists")
                                .show()
                    }
                }.create()
    }

    private lateinit var data: KotlinReactiveEntityStore<Persistable>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        data = (context as MainActivity).data
    }
}