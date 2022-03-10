package ips.software.musicplayerapprenticeship

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme
import ips.software.musicplayerapprenticeship.views.appLayout.AppLayout
import java.io.File
import java.io.FileFilter


class MainActivity : ComponentActivity() {
    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicPlayerApprenticeshipTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppLayout()
                }
            }
        }
        if (checkAndRequestPermissions()) {
            val directory: File = File(Environment.getExternalStoragePublicDirectory("Music").toString())
            var mp3Files: Array<File> = directory.listFiles { pathname -> pathname.name.endsWith(".mp3") }

            mp3Files.forEach {

            }
        } else {
            Toast.makeText(applicationContext, "Storage permission not granted.", Toast.LENGTH_LONG).show()
        }
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
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MusicPlayerApprenticeshipTheme {
        AppLayout()
    }
}