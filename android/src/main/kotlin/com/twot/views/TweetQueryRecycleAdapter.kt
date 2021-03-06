package com.twot.views

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.twot.MainActivity
import com.twot.R
import com.twot.core.models.*
import io.requery.Persistable
import io.requery.android.QueryRecyclerAdapter
import io.requery.kotlin.desc
import io.requery.kotlin.eq
import io.requery.query.Result
import io.requery.reactivex.KotlinReactiveEntityStore
import java.text.SimpleDateFormat

class TweetQueryRecycleAdapter(val data: KotlinReactiveEntityStore<Persistable>, val context: MainActivity) : QueryRecyclerAdapter<Tweet, TweetQueryRecycleAdapter.TweetHolder>(Models.DEFAULT, Tweet::class.java) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, index: Int): TweetHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.tweet_layout, viewGroup, false)
        return TweetQueryRecycleAdapter.TweetHolder(v)
    }

    override fun onBindViewHolder(tweet: Tweet, holder: TweetHolder, index: Int) {
        holder.tweetContent.text = tweet.content
        holder.tweetTitle.text = context.getString(R.string.tweetTitle, tweet.creator)
        holder.tweetDate?.text = tweet.pubDate.tweetFormat()

        Picasso.get().load(tweet.imageUrl).resize(120,120).into(holder.sourceIcon)

        when (tweet.tweetSource.type) {
            TweetSourceType.ACCOUNT -> holder.tweetSource.text = "@" + tweet.tweetSource.title
            TweetSourceType.SEARCH_TERM -> holder.tweetSource.text = "#" + tweet.tweetSource.title
        }

        //long click should open the tweet in browser
        holder.itemView.setOnLongClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tweet.link))
            context.startActivity(browserIntent)
            true
        }
    }

    override fun performQuery(): Result<Tweet> {
        return data.select(Tweet::class).join(TweetSource::class).on(Tweet::tweetSource eq TweetSource::title).where(TweetSource::enabled eq true).orderBy(Tweet::pubDate.desc()).get()
    }

    class TweetHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tweetContent = itemView.findViewById<TextView>(R.id.tweetContent)
        val tweetSource = itemView.findViewById<TextView>(R.id.tweetSource)
        val tweetTitle = itemView.findViewById<TextView>(R.id.tweetTitle)
        val tweetDate = itemView.findViewById<TextView>(R.id.tweetDate)
        val sourceIcon = itemView.findViewById<ImageView>(R.id.sourceIcon)
    }
}