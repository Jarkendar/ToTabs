package com.jarkendar.totabs

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import java.io.File

class MusicFileHolder(public val musicFile: File, private val context: Context) {

    private lateinit var name: String
    private lateinit var title: String
    private lateinit var artist: String
    private lateinit var author: String
    private lateinit var album: String
    private lateinit var genre: String
    private lateinit var duration: String

    private lateinit var mimeType: String
    private lateinit var bitRate: String
    private lateinit var modifyDate: String
    private var sampleRate: Int = 0
    private var channelCount: Int = 0
    private lateinit var numberOfTracks: String

    init {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(musicFile.absolutePath)
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(musicFile.absolutePath)

        pullBasicInfo(metadataRetriever)
        if (mediaExtractor.trackCount > 0) {
            pullAdvanceInfo(metadataRetriever, mediaExtractor.getTrackFormat(0))
        }
    }

    private fun pullBasicInfo(metadataRetriever: MediaMetadataRetriever) {
        name = musicFile.name
        title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
        artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
        author = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR) ?: ""
        album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""
        genre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""
        duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?: ""
    }

    private fun pullAdvanceInfo(metadataRetriever: MediaMetadataRetriever, mediaFormat: MediaFormat) {
        mimeType = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                ?: ""
        bitRate = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                ?: ""
        modifyDate = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                ?: ""
        sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        channelCount = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        numberOfTracks = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)
                ?: ""
    }

    //todo bold headers
    public fun getBasicInfo(): String {
        val basicInfo: String = "${context.resources.getString(R.string.basic_tag_text)}\n\n" +
                "${context.resources.getString(R.string.name_text)} @$name@\n" +
                "${context.resources.getString(R.string.title_text)} @$title@\n" +
                "${context.resources.getString(R.string.artist_text)} @$artist@\n" +
                "${context.resources.getString(R.string.author_text)} @$author@\n" +
                "${context.resources.getString(R.string.album_text)} @$album@\n" +
                "${context.resources.getString(R.string.genre_text)} @$genre@\n" +
                "${context.resources.getString(R.string.duration_text)} @$duration@"//todo correct time format
        return basicInfo.replace("@@", context.applicationContext.getString(R.string.empty_text)).replace("@", "")
    }

    public fun getAdvanceInfo(): String {
        val advanceInfo: String = "${context.resources.getString(R.string.advance_tag_text)}\n\n" +
                "${context.resources.getString(R.string.mime_type_text)} @$mimeType@\n" +
                "${context.resources.getString(R.string.bit_rate_text)} @$bitRate@\n" +//todo add units
                "${context.resources.getString(R.string.modify_date_text)} @$modifyDate@\n" +
                "${context.resources.getString(R.string.sample_rate_text)} @$sampleRate@\n" +//todo add units
                "${context.resources.getString(R.string.channel_count_text)} @$channelCount@\n" +
                "${context.resources.getString(R.string.number_of_tracks_text)} @$numberOfTracks@"
        return advanceInfo.replace("@@", context.applicationContext.getString(R.string.empty_text)).replace("@", "")
    }

}