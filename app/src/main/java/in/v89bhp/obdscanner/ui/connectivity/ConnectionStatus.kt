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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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


        ConnectionStatusHints(
            obdAdapterStatusHint = "Connected",
            vehicleECUStatusHint = "Disconnected"
        ) // TODO
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

@Composable
fun ConnectionStatusHints(
    obdAdapterStatusHint: String,
    vehicleECUStatusHint: String,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val (obdAdapter, colon1, colon2, obdAdapterStatus, vehicleECU, vehicleECUStatus) = createRefs()

        Text(text = "OBD Adapter",
            modifier = Modifier.constrainAs(obdAdapter) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            })

        Text(text = ":",
            modifier = Modifier.constrainAs(colon1) {
                start.linkTo(obdAdapter.end, margin = 8.dp)
                top.linkTo(obdAdapter.top)
            })

        Text(text = obdAdapterStatusHint,
            modifier = Modifier.constrainAs(obdAdapterStatus) {
                start.linkTo(colon1.end, margin = 8.dp)
                top.linkTo(colon1.top)
            })

        Text(text = "Vehicle ECU",
            modifier = Modifier.constrainAs(vehicleECU) {
                start.linkTo(obdAdapter.start)
                top.linkTo(obdAdapter.bottom, margin = 8.dp)
            })

        Text(text = ":",
            modifier = Modifier.constrainAs(colon2) {
                start.linkTo(colon1.start)
                top.linkTo(vehicleECU.top)
            })

        Text(text = vehicleECUStatusHint,
            modifier = Modifier.constrainAs(vehicleECUStatus) {
                start.linkTo(obdAdapterStatus.start)
                top.linkTo(colon2.top)
            })
    }
}
