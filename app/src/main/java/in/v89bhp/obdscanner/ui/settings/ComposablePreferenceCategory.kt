package `in`.v89bhp.obdscanner.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComposablePreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(start = 50.dp, top = 16.dp, bottom = 16.dp),
        color = MaterialTheme.colorScheme.primary
    )

    content()
}