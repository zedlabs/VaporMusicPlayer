package ml.zedlabs.vapormusicplayer.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ml.zedlabs.vapormusicplayer.data.entities.Song
import ml.zedlabs.vapormusicplayer.util.Constants.SONG_COLLECTION

/**
*   song list is static on the server side so mutable data
*   structures and state variables are not necessary
*/

class MusicDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songCollection = firestore.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception){
            emptyList()
        }
    }
}