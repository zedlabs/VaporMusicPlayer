package ml.zedlabs.vapormusicplayer.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import ml.zedlabs.vapormusicplayer.R
import ml.zedlabs.vapormusicplayer.data.entities.Song
import ml.zedlabs.vapormusicplayer.exoplayer.isPlaying
import ml.zedlabs.vapormusicplayer.exoplayer.toSong
import ml.zedlabs.vapormusicplayer.util.Status
import ml.zedlabs.vapormusicplayer.viewModels.MainViewModel
import ml.zedlabs.vapormusicplayer.viewModels.SongViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    private var shouldUpdateSeekbar = true


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers(view)

        val ivPlayPauseDetail = view.findViewById<ImageView>(R.id.ivPlayPauseDetail)
        ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        val seekBar  = view.findViewById<SeekBar>(R.id.seekBar)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    setCurPlayerTimeToTextView(progress.toLong(), view)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }
        })

        val ivSkipPrevious = view.findViewById<ImageView>(R.id.ivSkipPrevious)
        val ivSkip = view.findViewById<ImageView>(R.id.ivSkip)

        ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
    }

    private fun updateTitleAndSongImage(song: Song, tvSongName: TextView, ivSongImage: ImageView) {
        val title = "${song.title} - ${song.artist}"

        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers(view: View) {

        val tvSongDuration = view.findViewById<TextView>(R.id.tvSongDuration)
        val sb = view.findViewById<SeekBar>(R.id.seekBar)
        val tv = view.findViewById<TextView>(R.id.tvSongName)
        val iv = view.findViewById<ImageView>(R.id.ivSongImage)
        val ivPlayPauseDetail = view.findViewById<ImageView>(R.id.ivPlayPauseDetail)

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
        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            ivPlayPauseDetail.setImageResource(
                if(playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            sb.progress = it?.position?.toInt() ?: 0
        }
        songViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if(shouldUpdateSeekbar) {
                sb.progress = it.toInt()
                setCurPlayerTimeToTextView(it, view)
            }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner) {
            sb.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            tvSongDuration.text = dateFormat.format(it - (30*60*1000))
        }
    }
    private fun setCurPlayerTimeToTextView(ms: Long, view: View) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val tvCurTime = view.findViewById<TextView>(R.id.tvCurTime)
        tvCurTime.text = dateFormat.format(ms - (30*60*1000))
    }
}


