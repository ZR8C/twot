package com.twot.desktop.controllers

import com.twot.core.controllers.CoreController
import com.twot.core.models.*
import com.twot.core.models.TweetSourceType.*
import com.twot.desktop.views.TwotMainView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.requery.Persistable
import io.requery.kotlin.asc
import io.requery.kotlin.desc
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.image.Image
import org.sqlite.SQLiteDataSource
import tornadofx.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class TwotController : Controller() {

    companion object {
        //todo switch
        private val dataSource = SQLiteDataSource()

        init {
            dataSource.url = "jdbc:sqlite:desktop.db"

            SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)
        }

        private val configuration = KotlinConfiguration(model = Models.DEFAULT, dataSource = dataSource, useDefaultLogging = false)

        val data = KotlinReactiveEntityStore<Persistable>(KotlinEntityDataStore(configuration))
    }

    val mainView: TwotMainView by inject()
    private val coreController = CoreController(data)

    val accounts: ObservableList<TweetSource> = observableList()
    val tweets: ObservableList<Tweet> = observableList()

    private val imageCache = mutableMapOf<String, Image>()

    private var lastUpdated = LocalDateTime.now()
    var updateDuration = SimpleObjectProperty(Duration.ofSeconds(60))
    var disposableUpdater = Observable.interval(1, 1, TimeUnit.SECONDS)
            .subscribe {
                if (Duration.between(lastUpdated, LocalDateTime.now()) >= updateDuration.get()) {
                    log.fine("interval running")
                    fetchNewTweets()
                }
            }


    init {
         updateDuration.onChange { duration ->
             disposableUpdater.dispose()

             disposableUpdater = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribe {
                        if (Duration.between(lastUpdated, LocalDateTime.now()) >= duration) {
                            println("interval running")
                            fetchNewTweets()
                        }
                    }
        }

        data.select(Tweet::class).join(TweetSource::class).on(Tweet::tweetSource eq TweetSource::title).where(TweetSource::enabled eq true).orderBy(Tweet::pubDate.desc())
                .get()
                .observableResult()
                .subscribe {
                    runLater {
                        log.info { "Table change detected for tweets, refreshing tweets" }
                        tweets.setAll(it.toList())

                        val unread = tweets.count { !it.viewed }
                        if (unread > 0)
                            mainView.title = "Twot ($unread)"
                    }
                }

        data.select(TweetSource::class).orderBy(TweetSource::lastUpdated.asc())
                .get()
                .observableResult()
                .subscribe {
                    runLater {
                        log.info { "Table change detected for tweet sources" }
                        accounts.setAll(it.toList())
                    }
                }
    }

    /**
     * Get image from a url, caches the result
     */
    fun getImage(url: String?) : Image? {
        if (url == null) return null;
        return imageCache.getOrPut(url) {
            log.fine { "Loading image from url $url" }
            Image(url)
        }
    }

    fun fetchNewTweets() {
        runAsync {
            val lastUpdatedSource = coreController.getEnabledLastUpdatedSource()

            if (lastUpdatedSource != null) {
                log.fine { "Fetching updates for ${lastUpdatedSource.title}"}
                fetchNewTweets(lastUpdatedSource)
            }

        }
    }

    fun fetchNewTweets(source: TweetSource): List<Tweet> {
        lastUpdated = LocalDateTime.now()
        return coreController.getLatestTweets(source)
    }


    fun deleteSource(source: TweetSource) {
        log.fine {"Deleting tweetSource $source"}

        runAsync {
            data.delete(source).blockingGet()
        }
    }

    fun addSource(title: String, type: TweetSourceType = ACCOUNT): Boolean {
        log.fine { "Trying to add source $title" }

        if (data.count(TweetSource::class).where(TweetSource::title eq title).and(TweetSource::type eq type).get().value() != 0)
            return false

        runAsync {
            val source = TweetSourceEntity()

            with (source) {
                this.title = title
                enabled = true
                this.type = type
            }

            data.insert(source).blockingGet()

            log.fine {"Added source $title"}

            coreController.getLatestTweets(source)
        }

        return true
    }

    fun setAllTweetsRead() {
        log.fine {"setting all tweets to viewed"}

        runAsync {
            data.withTransaction {
                tweets.filter{ it.viewed == false }.forEach {
                    it.viewed = true
                    update(it)
                }
            }.blockingGet()
        } ui {
            mainView.title = "Twot"
        }
    }



}