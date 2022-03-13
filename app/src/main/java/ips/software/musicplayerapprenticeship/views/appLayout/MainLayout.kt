package ips.software.musicplayerapprenticeship.views.appLayout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ips.software.musicplayerapprenticeship.models.SongModel
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme
import ips.software.musicplayerapprenticeship.views.customviews.CustomTextNormalFont


class MainLayout {
    @Preview(showBackground = true)
    @Composable
    fun AppLayout_Preview() {
        MusicPlayerApprenticeshipTheme {
        }
    }
}