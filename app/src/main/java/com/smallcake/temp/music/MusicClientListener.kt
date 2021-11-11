package com.smallcake.temp.music

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat

interface MusicClientListener {
    fun onPlaybackStateChanged(state: PlaybackStateCompat)
    fun onMetadataChanged(metadata: MediaMetadataCompat)
    fun onProgress(currentDuration: Int,totalDuration:Int)
}