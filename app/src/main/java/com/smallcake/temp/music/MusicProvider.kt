package com.smallcake.temp.music

import android.database.Cursor
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import com.smallcake.temp.MyApplication


object MusicProvider {
    /**
     * 获取本地音乐
    {
        "albumId": 78,
        "duration": 192888,
        "id": 349675,
        "name": "矢野立美 - 3000万年前からのメッセージ ＜M-44,Tamのみ,M-45＞.mp3",
        "path": "/storage/emulated/0/netease/cloudmusic/Music/矢野立美 - 3000万年前からのメッセージ ＜M-44,Tamのみ,M-45＞.mp3",
        "singer": "矢野立美",
        "size": 4631636
    }
     */
    fun getLocaMusic():ArrayList<Song>{
        //要查询的数据
        val projection = arrayOf(
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID,
        )
        val selection = MediaStore.Audio.Media.MIME_TYPE + "=?"//查询条件
        val selectionArgs =  arrayOf("audio/mpeg")//符合条件的值
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val cursor: Cursor? =  MyApplication.instance.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        val list:ArrayList<Song> = ArrayList()
        cursor?.apply {
            while (moveToNext()) {
                val name     = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val path     = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val singer   = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val duration = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val id       = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val size     = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                val albumId  = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val song = Song(name,singer,size,duration,path,albumId,id)
                if (size>0&&duration>1000)
                list.add(song)
            }
            close()
        }
        return list
    }

    /**
     * Song转换为MediaMetadataCompat
     * @param song Song
     * @return MediaMetadataCompat
     */
    private fun transMediaMetadata(song: Song): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.path)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.name)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.singer)
            .build()
    }
    /**
     * Song转换为MediaMetadataCompat.MediaItem
     * @param song Song
     * @return MediaMetadataCompat
     */
     fun transMediaItem(song: Song): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(
            transMediaMetadata(song).description,
            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        )
    }
}