package ml.zedlabs.vapormusicplayer.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import ml.zedlabs.vapormusicplayer.R
import ml.zedlabs.vapormusicplayer.data.entities.Song
import javax.inject.Inject


class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : BaseSongAdapter(R.layout.list_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val tv1 = this.findViewById<TextView>(R.id.tvPrimary)
            val tv2 = this.findViewById<TextView>(R.id.tvSecondary)
            val ivItemImage = this.findViewById<ImageView>(R.id.ivItemImage)
            tv1.text = song.title
            tv2.text = song.artist
            glide.load(song.imageUrl).into(ivItemImage)

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }

}