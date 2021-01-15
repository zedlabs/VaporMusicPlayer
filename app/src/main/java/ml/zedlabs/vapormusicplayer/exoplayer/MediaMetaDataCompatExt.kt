package ml.zedlabs.vapormusicplayer.exoplayer

import android.support.v4.media.MediaMetadataCompat
import ml.zedlabs.vapormusicplayer.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.mediaId ?: "",
            it.title.toString(),
            it.subtitle.toString(),
            it.iconUri.toString(),
            it.mediaUri.toString()
        )
    }
}