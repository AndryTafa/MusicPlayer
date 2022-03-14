package ips.software.musicplayerapprenticeship.models

data class SongModel(
    val id: String = "Unknown",
    val title: String = "Unknown",
    val albums: String = "Unknown",
    val artist: String = "Unknown",
    val duration: Long = 0,
    var path: String = "Unknown"
)
