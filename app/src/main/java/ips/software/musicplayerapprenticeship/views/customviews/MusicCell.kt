package ips.software.musicplayerapprenticeship.views.customviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ips.software.musicplayerapprenticeship.models.SongModel
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme

@Composable
fun MusicCellComp(song: SongModel) {
    Card(
        modifier = Modifier.padding(10.dp),
        elevation = 5.dp,
        shape = RoundedCornerShape(10.dp)
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
                        ),
                ) {
                    CustomTextNormalFont(song.title)
                    CustomSmallTextNormalFont(song.artist)
                }
                Column( ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Actions",
                    )
                }
            }
        }
    }
}

class MusicCell {
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MusicPlayerApprenticeshipTheme {
        }
    }
}
