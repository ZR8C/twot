package com.twot

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.twot.R.id.*
import com.twot.R.layout.*
import com.twot.core.controllers.CoreController
import com.twot.core.models.*
import com.twot.fragments.AddTermDialog
import com.twot.fragments.AddUserDialog
import com.twot.fragments.ChangeRefreshDialog
import com.twot.views.SourceQueryRecycleAdapter
import com.twot.views.TweetQueryRecycleAdapter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.android.QueryRecyclerAdapter
import io.requery.android.sqlite.DatabaseSource
import io.requery.kotlin.desc
import io.requery.kotlin.eq
import io.requery.query.Result
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.TableCreationMode
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * todo
 * add collapsible menu bar
 * notifications
 * combine add dialogs
 * fix logging messages
 */
class MainActivity : AppCompatActivity() {

    val data : KotlinReactiveEntityStore<Persistable> by lazy {
        val source = DatabaseSource(this, Models.DEFAULT, 6)
        source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS)
        source.setLoggingEnabled(true)
        KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(source.configuration))
    }

    val coreController: CoreController by lazy { CoreController(data)}

    private lateinit var tweetRecycleAdapter: TweetQueryRecycleAdapter
    private lateinit var sourceRecycleAdapter: SourceQueryRecycleAdapter
    private lateinit var executor: ExecutorService

    lateinit var refresher: Disposable
    lateinit var tweetRecyclerSubscription: Disposable
    lateinit var sourceRecyclerSubscription: Disposable

    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(activity_main)
//        setSupportActionBar(my_toolbar)

        preferences = getPreferences(Context.MODE_PRIVATE)
        if (!preferences.contains("refresh_rate"))
            with(preferences.edit()) {
                putLong("refresh_rate", 60)
                commit()
            }

        executor = Executors.newSingleThreadExecutor()

        //set up tweet recycler
        tweetRecycleAdapter = TweetQueryRecycleAdapter(data, this)
        tweetRecycleAdapter.setExecutor(executor)

        tweetRecycler.adapter = tweetRecycleAdapter
        tweetRecycler.layoutManager = LinearLayoutManager(this)

        //set up source recycler
        sourceRecycleAdapter = SourceQueryRecycleAdapter(data, this)
        sourceRecycleAdapter.setExecutor(executor)

        sourceRecycler.adapter = sourceRecycleAdapter
        sourceRecycler.layoutManager = LinearLayoutManager(this)

        //update refresh button to show current refresh rate
        val refreshRate = preferences.getLong("refresh_rate", 60)
        change_refresh_btn.text = getString(R.string.refreshRate, refreshRate)

        refresher = Observable.interval(1, refreshRate, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    val source = coreController.getEnabledLastUpdatedSource()
                    if (source != null) {
                        println("Performing update for ${source.title}")
                        coreController.getLatestTweets(source)
                    }
                }

        add_user_btn.setOnClickListener {
            AddUserDialog().show(fragmentManager, "Add User Dialog")
        }

        add_term_btn.setOnClickListener{
            AddTermDialog().show(fragmentManager, "Add Term Dialog")
        }

        change_refresh_btn.setOnClickListener{
            ChangeRefreshDialog().show(fragmentManager, "Change Refresh Dialog")
        }

        //todo is there no way for query recycler to do this itself?
        //set up recyclers to update on table changes
        tweetRecyclerSubscription = data.select(Tweet::class).join(TweetSource::class).on(Tweet::tweetSource eq TweetSource::title).where(TweetSource::enabled eq true).orderBy(Tweet::pubDate.desc()).get()
                .observableResult()
                .subscribe {
                    println("tweet table change detected")
                    tweetRecycleAdapter.queryAsync()
                }

        sourceRecyclerSubscription = data.select(TweetSource::class).get()
                .observableResult()
                .subscribeOn(Schedulers.io())
                .subscribe{
                    println("source table change detected")
                    sourceRecycleAdapter.queryAsync()
                }
    }

    fun setRefreshInterval(interval: Long) {
        refresher.dispose()
        change_refresh_btn.text = getString(R.string.refreshRate, interval)
        refresher = Observable.interval(1, interval, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    val source = coreController.getEnabledLastUpdatedSource()
                    if (source != null) {
                        println("Performing update for ${source.title}")
                        coreController.getLatestTweets(source)
                    }
                }
    }

    override fun onResume() {
        tweetRecycleAdapter.queryAsync()
        sourceRecycleAdapter.queryAsync()
        super.onResume()
    }

    override fun onDestroy() {
        executor.shutdown()
        tweetRecycleAdapter.close()
        sourceRecycleAdapter.close()
        tweetRecyclerSubscription.dispose()
        sourceRecyclerSubscription.dispose()
        data.close()
        super.onDestroy()
    }

}