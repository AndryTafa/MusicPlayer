package ips.software.musicplayerapprenticeship

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ips.software.musicplayerapprenticeship.models.SongModel
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme
import ips.software.musicplayerapprenticeship.views.customviews.CustomSmallTextNormalFont
import ips.software.musicplayerapprenticeship.views.customviews.CustomTextNormalFont
import java.io.File

class MainActivity : ComponentActivity() {

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
        lateinit var allSongs: ArrayList<SongModel>
        var expanded = mutableStateOf(false)
        var mediaPlayer = MediaPlayer()
        var currentSongPath = mutableStateOf("")
        var currentSongPaused = mutableStateOf(false)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAllSongs()
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
            if (songs.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomTextNormalFont(customText = "No songs found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                ) {
                    items(songs) {
                        MusicCellComp(song = it)
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MusicCellComp(song: SongModel) {
        MenuDropDown()
        Card(
            elevation = 0.dp,
            onClick = {
                if (currentSongPath.value == song.path) {
                    if (currentSongPaused.value) {
                        resumeSong()
                    } else {
                        pauseSong()
                    }
                } else {
                    playSong(song = song)
                }
            },
            content = {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
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
//                                        playSong(song = song)
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

    private fun playSong(song: SongModel) {
//        mediaPlayer.stop()
//        mediaPlayer = MediaPlayer()
        mediaPlayer.reset()
        currentSongPath.value = song.path
        currentSongPaused.value = false
        mediaPlayer.setDataSource(this, Uri.parse(currentSongPath.value))
        mediaPlayer.prepare()
        mediaPlayer.start()

    }

    private fun pauseSong() {
        mediaPlayer.pause()
        currentSongPaused.value = true
    }

    private fun resumeSong() {
        mediaPlayer.start()
        currentSongPaused.value = false
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MusicPlayerApprenticeshipTheme {
    }
}