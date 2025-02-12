package com.example

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.model.newClass.audio.Audio
import com.example.trainerapp.R


class Library_AudioAdapter(
    var splist: MutableList<Audio.AudioData>,
    var context: Context,
) :
    RecyclerView.Adapter<Library_AudioAdapter.MyViewHolder>() {
    var selected: Int = 0

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var audioUrl: String? = null
    private var currentPlayingPosition: Int = -1
    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Library_AudioAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.audio_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return splist.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var audioBackground: CardView = view.findViewById<CardView>(R.id.audioBackground)
        var tvFname: TextView = view.findViewById<View>(R.id.tv_test_name) as TextView
        var img_play: ImageView = view.findViewById<View>(R.id.img_play) as ImageView
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = splist[position]
        holder.tvFname.text = movie.name

        if (position == currentPlayingPosition && isPlaying) {
            holder.img_play.setImageResource(R.drawable.ic_pause)
        } else {
            holder.img_play.setImageResource(R.drawable.ic_play)
        }

        if (selectedPosition == position) {
            holder.audioBackground.setBackgroundResource(R.drawable.card_select)
            // Your custom background
        } else {
            holder.audioBackground.setBackgroundColor(context.resources.getColor(R.color.card_background))
            // Default background
        }

        holder.img_play.setOnClickListener {
            selected = movie.id!!
            Log.d("Audio Play", "id - ${movie.id}")
            if (position == currentPlayingPosition && isPlaying) {
                pauseAudio()
                holder.img_play.setImageResource(R.drawable.ic_play)
            } else {
                playNewAudio(holder, position, "https://4trainersapp.com" + movie.audio)
            }

        }
        holder.tvFname.setOnClickListener {
            selected = movie.id!!
            Log.d("Audio Play", "name - ${movie.name}")
            if (selectedPosition == position) {
                // Deselect if already selected
                selectedPosition = -1
                holder.audioBackground.setBackgroundResource(R.drawable.card_not_select)
            } else {
                // Select new card
                val previousSelectedPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousSelectedPosition) // Reset previous selection
                holder.audioBackground.setBackgroundResource(R.drawable.card_select)
            }
            notifyItemChanged(position)
        }
    }

    private fun playNewAudio(holder: MyViewHolder, position: Int, url: String) {
        if (mediaPlayer != null && isPlaying) {
            pauseAudio()
            notifyItemChanged(currentPlayingPosition)
        }
        audioUrl = url
        currentPlayingPosition = position
        holder.img_play.setImageResource(R.drawable.ic_pause)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            prepareAsync() // Prepare asynchronously for network audio
            setOnPreparedListener {
                start() // Start playback when ready
                this@Library_AudioAdapter.isPlaying = true
            }

            setOnCompletionListener {
                // Reset when audio completes
                holder.img_play.setImageResource(R.drawable.ic_play)
                this@Library_AudioAdapter.isPlaying = false
                currentPlayingPosition = -1
            }
        }
    }

    private fun pauseAudio() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            isPlaying = false
        }
    }

    private fun dismissPlayer() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
    }
//    fun releaseMediaPlayer() {
//        mediaPlayer.let {
//            if (it != null) {
//                if (it.isPlaying) {
//                    it.stop()
//                }
//            }
//            it!!.release()
//            mediaPlayer = null
//            isPlaying = false
//        }
//    }
//
//    override fun onViewRecycled(holder: MyViewHolder) {
//        super.onViewRecycled(holder)
//        if (holder.adapterPosition == currentPlayingPosition) {
//            releaseMediaPlayer()  // Release MediaPlayer resources if the view is recycled
//        }
//    }
//

}
