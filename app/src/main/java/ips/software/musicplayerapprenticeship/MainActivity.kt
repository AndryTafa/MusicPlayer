package ips.software.musicplayerapprenticeship

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ips.software.musicplayerapprenticeship.models.SongModel
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme
import ips.software.musicplayerapprenticeship.views.customviews.CustomSmallTextNormalFont
import ips.software.musicplayerapprenticeship.views.customviews.CustomTextNormalFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ArithmeticException
import java.lang.IllegalStateException

class MainActivity : ComponentActivity() {

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
        lateinit var allSongs: ArrayList<SongModel>
        var expanded = mutableStateOf(false)
        var mediaPlayer = MediaPlayer()
        var currentSong = mutableStateOf(SongModel())
        var isCurrentSongPaused = mutableStateOf(false)
        var isCurrentSongEnded = mutableStateOf(false)
        var mCurrentTimeStamp = mutableStateOf(0)
        var currentSongsQueue = mutableStateListOf(SongModel())
        var currentSongIndex = mutableStateOf(0)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAllSongs()
        updateCurrentTimeStamp()
        configureLayout()
    }

    private fun configureLayout() {
        setContent {
            MusicPlayerApprenticeshipTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        AppLayout(allSongs)
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun setAllSongs() {
        allSongs = getAllSongs()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("Recycle", "Range")
    private fun getAllSongs(): ArrayList<SongModel> {
        val tempList = ArrayList<SongModel>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA
        )

        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            ?: "Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                        ?: "Unknown"
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            ?: "Unknown"
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ?: "Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                    val music = SongModel(
                        id = idC,
                        title = titleC,
                        albums = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC
                    )
                    val file = File(music.path)
                    if (file.exists()) {
                        Log.d("MainActivity", "Song path: ${music.path}")
                        tempList.add(music)
                    }
                } while (cursor.moveToNext())
            cursor.close()
        }
        tempList.forEach {
            currentSongsQueue.add(it)
        }
        return tempList
    }

    private fun checkAndRequestPermissions(): Boolean {
        val write = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val manage = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (manage != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    @Composable
    fun AppLayout(songs: List<SongModel>) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!songs.isNullOrEmpty()) {
                val roundedCorners = 7.dp
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(2f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedButton(
                            onClick = {
                                playSongWithExceptionHandling(song = songs[0])
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .padding(vertical = 10.dp, horizontal = 10.dp),
                            shape = RoundedCornerShape(roundedCorners)
                        ) {
                            Text(text = "Play all")
                        }
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .padding(vertical = 10.dp, horizontal = 10.dp),
                            shape = RoundedCornerShape(roundedCorners)
                        ) {
                            Text(
                                text = "Shuffle",
                                color = Color.White
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(25f),
                    ) {
                        items(songs) {
                            MusicCellComp(song = it)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "😱", fontSize = 50.sp)
                    CustomTextNormalFont(customText = "You have no songs")
                }
            }
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MusicCellComp(song: SongModel) {
        Card(
            elevation = 0.dp,
            onClick = {
                if (currentSong.value.path == song.path) {
                    if (!isCurrentSongEnded.value) {
                        if (isCurrentSongPaused.value) {
                            resumeSong()
                        } else {
                            pauseSong()
                        }
                    } else {
                        playSongWithExceptionHandling(song)
                    }
                } else {
                    playSongWithExceptionHandling(song)
                }
            },
            content = {
                Column(
                    modifier = Modifier.padding(bottom = 10.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 3.dp
                                    )
                                    .weight(9f),
                            ) {
                                CustomTextNormalFont(song.title)
                                CustomSmallTextNormalFont(song.artist)
                            }
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 9.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        println("Icon clicked")
                                        Toast.makeText(
                                            applicationContext,
                                            "Three dots clicked",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        debugLogs()
                                    },
                                    content = {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = "More Actions",
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun MenuDropDown() {
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },

            ) {
            DropdownMenuItem(onClick = { /* Handle refresh! */ }) {
                Text("Refresh")
            }
            DropdownMenuItem(onClick = { /* Handle settings! */ }) {
                Text("Settings")
            }
            Divider()
            DropdownMenuItem(onClick = { /* Handle send feedback! */ }) {
                Text("Send Feedback")
            }
        }
    }

    private fun attemptStartSong(song: SongModel): Boolean {
        Log.d("MainActivity", "Started playing ${song.title}")
        return try {
            mediaPlayer.reset()
            currentSong.value.path = song.path
            isCurrentSongPaused.value = false
            isCurrentSongEnded.value = false
            mediaPlayer.setDataSource(this, Uri.parse(currentSong.value.path))
            mediaPlayer.prepare()
            mediaPlayer.start()
            debugLogs()
            true
        } catch (e: IllegalStateException) {
            Log.d("MainActivity", "Illegal state exception caught")
            false
        }
    }

    private fun debugLogs() {
        val demo: String = mediaPlayer.currentPosition.toString()
        val demoTwo: String = mediaPlayer.timestamp.toString()
        val demoThree: String = mediaPlayer.duration.toString()
        Log.d("MainActivity", "current position:\t$demo")
        Log.d("MainActivity", "timestamp:\t\t\t$demoTwo")
        Log.d("MainActivity", "duration:\t\t\t$demoThree")
        Log.d("MainActivity", "mCurrentTimeStamp:\t${mCurrentTimeStamp.value}")
        Log.d("MainActivity", "songPaused:\t\t\t${isCurrentSongPaused.value}")
        Log.d("MainActivity", "songEnded:\t\t\t${isCurrentSongEnded.value}")
    }

    private fun updateCurrentTimeStamp() {
        // update song timestamp on background thread
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                while (!isCurrentSongPaused.value) {
                    try {
                        mCurrentTimeStamp.value = mediaPlayer.currentPosition
                        if (mCurrentTimeStamp.value > 0 && mediaPlayer.duration > 0) {
                            try {
                                if (checkSongDidEnd()) {
                                    val demoIndex = getCurrentSongIndexInQueue()
                                    if (currentSongIndex.value != currentSongsQueue.size)
                                    playSongWithExceptionHandling(currentSongsQueue[getCurrentSongIndexInQueue() + 1])
                                }
                            } catch (e: ArithmeticException) {
                                Log.d("MainActivity", "Attempted to divide something by 0")
                                Log.d(
                                    "MainActivity", "Current variables:\n" +
                                            "mCurrentTimeStamp.value: ${mCurrentTimeStamp.value}" +
                                            "mediaPlayer.duration: ${mediaPlayer.duration}"
                                )
                            }
                        }
                        currentSongIndex.value = getCurrentSongIndexInQueue()
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    private fun getCurrentSongIndexInQueue(): Int {
        var songIndex = 1
        for (song in currentSongsQueue.withIndex()) {
            if (currentSong.value.path == song.value.path) {
                songIndex = song.index
            }
        }
        return songIndex
    }

    private fun checkSongDidEnd(): Boolean {
        if ((mCurrentTimeStamp.value / mediaPlayer.duration) >= 0.99) {
            isCurrentSongEnded.value = true
            return isCurrentSongEnded.value
        }
        return isCurrentSongEnded.value
    }

    private fun pauseSong() {
        mediaPlayer.pause()
        isCurrentSongPaused.value = true
        isCurrentSongEnded.value = false
        debugLogs()
    }

    private fun resumeSong() {
        mediaPlayer.start()
        mCurrentTimeStamp.value = mediaPlayer.currentPosition
        isCurrentSongEnded.value = false
        isCurrentSongPaused.value = false
        debugLogs()
    }


    private fun playSongWithExceptionHandling(song: SongModel) {
        if (!attemptStartSong(song = song)) {
            if (!attemptStartSong(song = song)) {
                if (!attemptStartSong(song = song)) {
                    Toast.makeText(
                        applicationContext,
                        "Couldn't play song, please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MusicPlayerApprenticeshipTheme {
    }
}