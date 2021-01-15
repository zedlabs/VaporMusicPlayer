package ml.zedlabs.vapormusicplayer.adapters


import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import ml.zedlabs.vapormusicplayer.R

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            val text = "${song.title} - ${song.artist}"
            val tvPrimary = this.findViewById<TextView>(R.id.tvPrimary)
            tvPrimary.text = text

            setOnClickListener {
                onItemClickListener?.let { click ->
                    click(song)
                }
            }
        }
    }

}
