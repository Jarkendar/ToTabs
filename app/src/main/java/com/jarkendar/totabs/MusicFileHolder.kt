package com.jarkendar.totabs

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import java.io.File

class MusicFileHolder(public val musicFile: File, private val context: Context) {

    private lateinit var title: String
    private lateinit var artist: String
    private lateinit var author: String
    private lateinit var album: String
    private lateinit var genre: String
    private lateinit var duration: String

    private var mimeType: String = ""
    private var bitRate: String = ""
    private var modifyDate: String = ""
    private var sampleRate: String = ""
    private var channelCount: String = ""
    private var numberOfTrack: String = ""

    init {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(musicFile.absolutePath)
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(musicFile.absolutePath)

        getBasicInfo(metadataRetriever)
        if (mediaExtractor.trackCount > 0) {
            getExtendInfo(metadataRetriever, mediaExtractor.getTrackFormat(0))
        }
    }

    private fun getBasicInfo(metadataRetriever: MediaMetadataRetriever) {
        title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        author = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR)
        album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        genre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
        duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    }

    private fun getExtendInfo(metadataRetriever: MediaMetadataRetriever, mediaFormat: MediaFormat) {
        mimeType = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        bitRate = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
        modifyDate = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
        sampleRate = if (mediaFormat.getString(MediaFormat.KEY_SAMPLE_RATE) != null) {
            mediaFormat.getString(MediaFormat.KEY_SAMPLE_RATE)
        } else {
            ""
        }
        channelCount = if (mediaFormat.getString(MediaFormat.KEY_CHANNEL_COUNT) == null) {
            mediaFormat.getString(MediaFormat.KEY_CHANNEL_COUNT)
        } else {
            ""
        }
        numberOfTrack = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)
    }

}