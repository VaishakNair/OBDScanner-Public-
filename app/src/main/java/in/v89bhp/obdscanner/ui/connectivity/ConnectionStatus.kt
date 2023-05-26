package `in`.v89bhp.obdscanner.ui.connectivity

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.ui.theme.PurpleGrey40

@Composable
fun ConnectionStatus(
    modifier: Modifier = Modifier,
    viewModel: ConnectionStatusViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)


    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextInCircle(text = "89 bhp")

            Divider(modifier = Modifier.width(45.dp))

            TextInCircle(text = "OBD Adapter")

            Divider(modifier = Modifier.width(45.dp))

            TextInCircle(text = "Vehicle ECU")
        }



        Row(modifier = Modifier.padding(4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.obd_adapter),
                modifier = Modifier.width(110.dp))
            Text(text = " : ")
            Text(text = "Connected",
                modifier = Modifier.width(110.dp)) // TODO
        }
        Row(modifier = Modifier.padding(4.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.vehicle_ecu),
                modifier = Modifier.width(110.dp))
            Text(text = " : ")
            Text(text = "Disconnected",
                modifier = Modifier.width(110.dp)) // TODO
        }
    }
}

@Composable
fun TextInCircle(text: String, background: Color = PurpleGrey40, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(CircleShape)
            .background(background)
            .size(80.dp)

    ) {
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = colorResource(id = android.R.color.white)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RowPreview() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)


    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextInCircle(text = "89 bhp")

            Divider(modifier = Modifier.width(45.dp))

            TextInCircle(text = "OBD Adapter")

            Divider(modifier = Modifier.width(45.dp))

            TextInCircle(text = "Vehicle ECU")
        }



        Row(modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.obd_adapter),
            modifier = Modifier.width(100.dp))
            Text(text = " : ")
            Text(text = "Connected",
                modifier = Modifier.width(100.dp)) // TODO
        }
        Row(modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.vehicle_ecu),
            modifier = Modifier.width(100.dp))
            Text(text = " : ")
            Text(text = "Disconnected",
                modifier = Modifier.width(100.dp)) // TODO
        }
    }
}