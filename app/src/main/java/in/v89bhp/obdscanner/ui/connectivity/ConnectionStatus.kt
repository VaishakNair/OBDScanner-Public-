package `in`.v89bhp.obdscanner.ui.connectivity

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.ui.theme.PurpleGrey40

@Composable
fun ConnectionStatus(
    modifier: Modifier = Modifier,
    viewModel: BluetoothConnectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextInCircle(text = "89 bhp", modifier = Modifier.background(PurpleGrey40))

            Divider(modifier = Modifier.width(45.dp))

            TextInCircle(text = "OBD Adapter", modifier = Modifier.background(PurpleGrey40))

            Divider(modifier = Modifier.width(45.dp))

            TextInCircle(text = "Vehicle ECU", modifier = Modifier.background(PurpleGrey40))
        }

    }
}

@Composable
fun TextInCircle(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(CircleShape)
            .size(90.dp)

    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = colorResource(id = android.R.color.white)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RowPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextInCircle(text = "89 bhp", modifier = Modifier.background(PurpleGrey40))
        Divider(modifier = Modifier.width(45.dp))
        TextInCircle(text = "OBD Adapter", modifier = Modifier.background(PurpleGrey40))
        Divider(modifier = Modifier.width(45.dp))
        TextInCircle(text = "Vehicle ECU", modifier = Modifier.background(PurpleGrey40))
    }
}