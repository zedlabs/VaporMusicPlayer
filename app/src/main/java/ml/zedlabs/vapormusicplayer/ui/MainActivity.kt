package ml.zedlabs.vapormusicplayer.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ml.zedlabs.vapormusicplayer.viewModels.MainViewModel
import ml.zedlabs.vapormusicplayer.R
import ml.zedlabs.vapormusicplayer.adapters.SwipeSongAdapter
import ml.zedlabs.vapormusicplayer.data.entities.Song
import ml.zedlabs.vapormusicplayer.exoplayer.isPlaying
import ml.zedlabs.vapormusicplayer.exoplayer.toSong
import ml.zedlabs.vapormusicplayer.util.Status.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToObservers()

        val  vpSong = findViewById<ViewPager2>(R.id.vpSong)
        vpSong.adapter = swipeSongAdapter

        vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        val ivPlayPause = findViewById<ImageView>(R.id.ivPlayPause)
        ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        val navHostFragment = findViewById<View>(R.id.navHostFragment)

        swipeSongAdapter.setItemClickListener {
            navHostFragment.findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            val uvCurSongImage = findViewById<ImageView>(R.id.ivCurSongImage)
            val vpSong = findViewById<ViewPager2>(R.id.vpSong)
            val ivPlayPause = findViewById<ImageView>(R.id.ivPlayPause)
            when(destination.id) {
                R.id.songFragment -> hideBottomBar(uvCurSongImage, vpSong, ivPlayPause)
                R.id.homeFragment -> showBottomBar(uvCurSongImage, vpSong, ivPlayPause)
                else -> showBottomBar(uvCurSongImage, vpSong, ivPlayPause)
            }
        }
    }

    private fun hideBottomBar(uvCurSongImage: ImageView, vpSong: ViewPager2, ivPlayPause: ImageView) {
        uvCurSongImage.isVisible = false
        vpSong.isVisible = false
        ivPlayPause.isVisible = false
    }

    private fun showBottomBar(uvCurSongImage: ImageView, vpSong: ViewPager2, ivPlayPause: ImageView) {
        uvCurSongImage.isVisible = true
        vpSong.isVisible = true
        ivPlayPause.isVisible = true
    }

    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            val  vpSong = findViewById<ViewPager2>(R.id.vpSong)
            vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        val ivCurSongImage = findViewById<ImageView>(R.id.ivCurSongImage)
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                    .into(ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    ERROR -> Unit
                    LOADING -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }
        val ivPlayPause = findViewById<ImageView>(R.id.ivPlayPause)

        mainViewModel.playbackState.observe(this) {
            playbackState = it
            ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    ERROR -> Snackbar.make(
                        findViewById(R.id.rootLayout),
                        result.message ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    ERROR -> Snackbar.make(
                        findViewById(R.id.rootLayout),
                        result.message ?: "An unknown error occured",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
    }
}

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent(R.layout.activity_main)
//
//        //{
////            VaporMusicPlayerTheme {
////                // A surface container using the 'background' color from the theme
////                Surface(color = MaterialTheme.colors.background) {
////                    Greeting("Android")
////                }
////            }
//        }
//    //}


//@Composable
//fun Greeting(name: String) {
//    Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    VaporMusicPlayerTheme {
//        Greeting("Android")
//    }
//}