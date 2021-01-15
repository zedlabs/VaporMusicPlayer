package ml.zedlabs.vapormusicplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import ml.zedlabs.vapormusicplayer.R
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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