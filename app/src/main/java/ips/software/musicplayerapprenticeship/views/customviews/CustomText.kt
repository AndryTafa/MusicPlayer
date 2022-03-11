package ips.software.musicplayerapprenticeship.views.customviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ips.software.musicplayerapprenticeship.ui.theme.MusicPlayerApprenticeshipTheme
import kotlin.reflect.typeOf


@Composable
fun CustomTextNormalFont(customText: String) {
    Text(
        text = customText,
        fontSize = 17.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun CustomSmallTextNormalFont(customText: String) {
    Text(
        text = customText,
        fontSize = 13.sp,
    )
}

class CustomText {
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MusicPlayerApprenticeshipTheme {
            Column() {
                CustomTextNormalFont(customText = "This is a demo.")
                CustomSmallTextNormalFont(customText = "This is a second demo.")
            }
        }
    }
}
