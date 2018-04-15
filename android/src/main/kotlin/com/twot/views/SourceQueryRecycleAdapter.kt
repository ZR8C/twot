package com.twot.views

import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.twot.MainActivity
import com.twot.R
import com.twot.core.models.Models
import com.twot.core.models.TweetSource
import com.twot.core.models.TweetSourceType
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.android.QueryRecyclerAdapter
import io.requery.query.Result
import io.requery.reactivex.KotlinReactiveEntityStore

class SourceQueryRecycleAdapter(val data: KotlinReactiveEntityStore<Persistable>, val context: MainActivity) : QueryRecyclerAdapter<TweetSource, SourceQueryRecycleAdapter.SourceHolder>(Models.DEFAULT, TweetSource::class.java) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, index: Int): SourceHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.source_layout, viewGroup, false)

        return SourceQueryRecycleAdapter.SourceHolder(v)
    }

    override fun onBindViewHolder(source: TweetSource, holder: SourceHolder, index: Int) {
        holder.title.text = when(source.type) {
            TweetSourceType.ACCOUNT -> "@${source.title}"
            TweetSourceType.SEARCH_TERM -> "#${source.title}"
        }

        if (!source.enabled)
            holder.title.alpha = 0.5f
        else
            holder.title.alpha = 1f

        // regular click should toggle enabled
        holder.itemView.setOnClickListener {
            println("$source was long clicked")

            source.enabled = !source.enabled
            data.update(source).blockingGet()
        }

        // long click should give context menu
        holder.itemView.setOnLongClickListener {
            println("$source was long clicked")

            val popupMenu = PopupMenu(context, holder.itemView)
            popupMenu.inflate(R.menu.source_popup)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_source -> {
                        AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to delete ${holder.title.text}?")
                                .setPositiveButton("Yes", { dialog,_ ->
                                    data.delete(source).blockingGet()
                                    dialog.dismiss()
                                })
                                .setNegativeButton("Cancel", { dialog,_ ->
                                    dialog.cancel()
                                }).show()
                        true
                    }
                    R.id.refresh_source -> {
                        Observable.fromCallable {
                            context.coreController.getLatestTweets(source)
                        }
                                .subscribeOn(Schedulers.io())
                                .subscribe()
                        true
                    }
                    else -> true
                }
            }

            true
        }
    }

    override fun performQuery(): Result<TweetSource> {
        return (data select (TweetSource::class)).get()
    }

    class SourceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.source_title)
    }
}