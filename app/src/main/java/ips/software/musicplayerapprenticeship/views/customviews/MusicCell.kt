package ips.software.musicplayerapprenticeship.views.customviews

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ips.software.musicplayerapprenticeship.models.SongModel
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme

@Composable
fun MusicCellComp(song: SongModel) {
    Column(
        modifier = Modifier.padding(top = 10.dp),
//        elevation = 3.dp,
//        shape = RoundedCornerShape(40.dp)
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

class MusicCell {
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MusicPlayerApprenticeshipTheme {
            MusicCellComp(song = SongModel())
        }
    }
}
