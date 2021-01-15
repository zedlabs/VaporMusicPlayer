package ml.zedlabs.vapormusicplayer.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import ml.zedlabs.vapormusicplayer.R
import ml.zedlabs.vapormusicplayer.data.entities.Song
import ml.zedlabs.vapormusicplayer.exoplayer.toSong
import ml.zedlabs.vapormusicplayer.util.Status
import ml.zedlabs.vapormusicplayer.viewModels.MainViewModel
import ml.zedlabs.vapormusicplayer.viewModels.SongViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers(view.findViewById<TextView>(R.id.tvSongName), view.findViewById(R.id.ivSongImage))
    }

    private fun updateTitleAndSongImage(song: Song, tvSongName: TextView, ivSongImage: ImageView) {
        val title = "${song.title} - ${song.artist}"

        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers(tv: TextView, iv: ImageView) {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            if(curPlayingSong == null && songs.isNotEmpty()) {
                                curPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0], tv, iv)
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if(it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!, tv, iv)
        }
    }
}


