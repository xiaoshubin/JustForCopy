package com.smallcake.temp.music

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.NonNull

abstract class MusicClientListener {
    abstract fun onPlaybackStateChanged(state: PlaybackStateCompat)
    abstract fun onMetadataChanged(metadata: MediaMetadataCompat)
    abstract fun onProgress(currentDuration: Int,totalDuration:Int)
    open fun onChildrenLoaded(@NonNull parentId: String, @NonNull children: List<MediaBrowserCompat.MediaItem>){

    }
}