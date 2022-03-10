package ips.software.musicplayerapprenticeship.views.appLayout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ips.software.musicplayerapprenticeship.models.SongModel
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme
import ips.software.musicplayerapprenticeship.views.customviews.MusicCellComp

@Composable
fun AppLayout() {
    var songListDemo = listOf(
        SongModel("Andry", "Demo"),
        SongModel("IPS", "Demo"),
        SongModel("IDK", "Demo"),
        SongModel("Andry", "IPS"),
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(songListDemo) {
                MusicCellComp(song = it)
            }
        }
    }
}

class MainLayout {
    @Preview(showBackground = true)
    @Composable
    fun AppLayout_Preview() {
        MusicPlayerApprenticeshipTheme {
            AppLayout()
        }
    }
}