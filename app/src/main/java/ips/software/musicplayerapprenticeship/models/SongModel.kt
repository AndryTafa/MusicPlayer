package ips.software.musicplayerapprenticeship.models

data class SongModel(
    val id: String,
    val title: String,
    val albums: String,
    val artist: String,
    val duration: Long = 0,
    val path: String
)
