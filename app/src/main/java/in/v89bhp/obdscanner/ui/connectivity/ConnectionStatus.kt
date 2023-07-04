package `in`.v89bhp.obdscanner.ui.connectivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavBackStackEntry
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.ElmHelper
import `in`.v89bhp.obdscanner.ui.theme.HoloGreenLight
import `in`.v89bhp.obdscanner.ui.theme.HoloRedLight
import `in`.v89bhp.obdscanner.ui.theme.PurpleGrey40

@Composable
fun ConnectionStatus(
    backStackEntry: NavBackStackEntry,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConnectionStatusViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    )
) {


    Box(modifier = modifier) {
        if (viewModel.isConnecting) {
            CancellableCircularProgress(text = stringResource(R.string.connecting),
                onCancel = { viewModel.cancel() })
        } else if (viewModel.isError) {// Error card for bluetooth connection errors:
            ErrorCard(
                errorMessage = viewModel.errorMessage,
                onClick = { viewModel.loadConnectionStatus() })

        } else {
            // Contains the three circles and other text showing connection status from 89 bhp to OBD adapter and vehicle ECU
            ConnectionStatusCard(
                navigateBack = navigateBack,
                onTryAgain = {
                viewModel.loadConnectionStatus()
            })
        }
    }


}

@Composable
fun ErrorCard(errorMessage: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.padding(8.dp)
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Text(text = stringResource(R.string.try_again))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionStatusCardPreview() {
    ConnectionStatusCard({}, {})
}

@Composable
fun ConnectionStatusCard(
    navigateBack: () -> Unit,
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isElmInitialized = ElmHelper.elmInitialized.value as Boolean
    val isECUInitialized = ElmHelper.ecuInitialized.value as Boolean


    Card(
        modifier = modifier
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
            TextInCircle(
                text = stringResource(id = R.string.app_name),
                background = HoloGreenLight
            )

            Divider(
                modifier = Modifier.width(45.dp),
                color = if (isElmInitialized) HoloGreenLight else PurpleGrey40
            )

            TextInCircle(
                text = stringResource(id = R.string.obd_adapter),
                background = if (isElmInitialized) HoloGreenLight else PurpleGrey40
            )

            Divider(
                modifier = Modifier.width(45.dp),
                color = if (isECUInitialized) HoloGreenLight else PurpleGrey40
            )

            TextInCircle(
                text = stringResource(id = R.string.vehicle_ecu),
                background = if (isECUInitialized) HoloGreenLight else PurpleGrey40
            )
        }


        ConnectionStatusHints(
            obdAdapterStatusHint = stringResource(id = if (isElmInitialized) R.string.connected else R.string.disconnected),
            vehicleECUStatusHint = stringResource(id = if (isECUInitialized) R.string.connected else R.string.disconnected)
        )


        if (isElmInitialized) {
            if (isECUInitialized) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                Text(
                    text = stringResource(R.string.connection_successful),
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = navigateBack,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                ) {
                    Text(text = stringResource(R.string.done))
                }}
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = stringResource(R.string.ign_off_error))
                    Button(
                        onClick = onTryAgain,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.try_again))
                    }
                }
            }
        } else {
            Button(
                onClick = onTryAgain,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = stringResource(R.string.connect))
            }
        }
    }
}

@Composable
fun TextInCircle(text: String, modifier: Modifier = Modifier, background: Color = PurpleGrey40) {
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

        Text(text = stringResource(R.string.obd_adapter),
            modifier = Modifier.constrainAs(obdAdapter) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            })

        Text(text = stringResource(R.string.colon),
            modifier = Modifier.constrainAs(colon1) {
                start.linkTo(obdAdapter.end, margin = 8.dp)
                top.linkTo(obdAdapter.top)
            })

        Text(
            text = obdAdapterStatusHint,
            modifier = Modifier.constrainAs(obdAdapterStatus) {
                start.linkTo(colon1.end, margin = 8.dp)
                top.linkTo(colon1.top)
            },
            color = if (obdAdapterStatusHint == stringResource(id = R.string.connected)) HoloGreenLight else HoloRedLight
        )

        Text(text = stringResource(R.string.vehicle_ecu),
            modifier = Modifier.constrainAs(vehicleECU) {
                start.linkTo(obdAdapter.start)
                top.linkTo(obdAdapter.bottom, margin = 8.dp)
            })

        Text(text = stringResource(R.string.colon),
            modifier = Modifier.constrainAs(colon2) {
                start.linkTo(colon1.start)
                top.linkTo(vehicleECU.top)
            })

        Text(
            text = vehicleECUStatusHint,
            modifier = Modifier.constrainAs(vehicleECUStatus) {
                start.linkTo(obdAdapterStatus.start)
                top.linkTo(colon2.top)
            },
            color = if (vehicleECUStatusHint == stringResource(id = R.string.connected)) HoloGreenLight else HoloRedLight
        )
    }
}

@Composable
fun CancellableCircularProgress(text: String, onCancel: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
        Text(
            text = text,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            onClick = onCancel,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}
