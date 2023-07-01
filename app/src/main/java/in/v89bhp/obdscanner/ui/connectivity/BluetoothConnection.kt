package `in`.v89bhp.obdscanner.ui.connectivity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper

@Composable
fun BluetoothConnection(
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = backStackEntry,
    viewModel: BluetoothConnectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    ),
    connectionSetupPagerViewModel: ConnectionSetupPagerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    )
) {

    // If `lifecycleOwner` changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updateBtEnabledStatus()
                viewModel.queryPairedDevices()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current

    SystemBroadcastReceiver(BluetoothAdapter.ACTION_STATE_CHANGED) { intent ->
        viewModel.updateBtEnabledStatus()
    }
    if (BluetoothHelper.connecting) {
        CircularProgress(text = stringResource(R.string.connecting))
    } else {
        if (!viewModel.isBtEnabled) {

            TurnBluetoothOn({ viewModel.turnBluetoothOn(context) })
            connectionSetupPagerViewModel.isNextButtonEnabled = false
        } else {
            viewModel.queryPairedDevices()

            PairedDevices(
                pairedDevices = viewModel.pairedDevices,
                onPair = { viewModel.showBluetoothSettings(context) },
                onUpdateNextButtonEnabled = { enabled ->
                    connectionSetupPagerViewModel.isNextButtonEnabled = enabled
                },
                onConnect = { bluetoothDevice -> viewModel.connect(bluetoothDevice) })

        }
    }
    // Error Dialog for bluetooth connection errors:
    if (viewModel.showErrorDialog) {

        AlertDialog(onDismissRequest = { viewModel.showErrorDialog = false },
            title = {
                Text(text = stringResource(id = R.string.error))
            },
            text = {
                Text(text = viewModel.errorMessage)
            },
            confirmButton = { },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.showErrorDialog = false
                    }
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            })
    }
}

@Composable
fun SystemBroadcastReceiver(
    systemAction: String,
    onSystemEvent: (intent: Intent?) -> Unit
) {
    // Grab the current context in this part of the UI tree
    val context = LocalContext.current

    // Safely use the latest onSystemEvent lambda passed to the function
    val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)

    // If either context or systemAction changes, unregister and register again
    DisposableEffect(context, systemAction) {
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentOnSystemEvent(intent)
            }
        }

        context.registerReceiver(broadcast, intentFilter)

        // When the effect leaves the Composition, remove the callback
        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }
}

@Composable
fun TurnBluetoothOn(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.bluetooth_off),
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.padding(8.dp)
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        ) {
            Text(text = stringResource(R.string.turn_bluetooth_on))
        }
    }
}

@Composable
fun CircularProgress(text: String, modifier: Modifier = Modifier) {
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
    }
}

@Composable
fun PairedDevices(
    pairedDevices: List<BluetoothDevice>,
    onPair: () -> Unit,
    onUpdateNextButtonEnabled: (Boolean) -> Unit,
    onConnect: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pairedDevices.isEmpty()) {// No paired devices:
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.no_paired_bluetooth_devices),
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier.padding(8.dp)
            )
            Button(
                onClick = onPair,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = stringResource(R.string.pair))
            }
        }
    } else {
        Column() {

            PairedDeviceHintCard(pairedDevices, onPair, onUpdateNextButtonEnabled)

            val bluetoothSocket = BluetoothHelper.socket
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.padding(16.dp)
            ) {
                try {
                    items(pairedDevices, key = { it.address }) { bluetoothDevice ->
                        val connected =
                            bluetoothSocket?.let { it.isConnected && it.remoteDevice.name == bluetoothDevice!!.name }
                                ?: false
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!connected) {
                                    onConnect(bluetoothDevice)
                                }
                            }) {
                            Text(
                                text = bluetoothDevice.name,

                                )
                            Text(
                                text = if (connected) "Connected" else "Disconnected",
                                color = colorResource(
                                    id = if (connected) android.R.color.holo_green_light else
                                        android.R.color.holo_red_light
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Divider(modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                } catch (ex: SecurityException) {
                    Log.e("BluetoothConnection", "Bluetooth permission(s) not granted", ex)

                }
            }
        }
    }
}


@Composable
fun PairedDeviceHintCard(
    pairedDevices: List<BluetoothDevice>,
    onPair: () -> Unit,
    updateNextButtonEnabled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        if (pairedDevices.isNotEmpty()) {
            val bluetoothSocket =
                BluetoothHelper.socket // Socket that's currently in the bt helper. It may be null, connected or disconnected
            var connected = false
            var connectedDeviceName: String? = null
            try {
                for (bluetoothDevice in pairedDevices) {
                    bluetoothSocket?.let {
                        if (it.isConnected && it.remoteDevice.name == bluetoothDevice.name) {
                            connected = true
                            connectedDeviceName = bluetoothDevice.name
                        }
                    }
                }
            } catch (ex: SecurityException) {
                Log.e("BluetoothConnection", "Bluetooth permission(s) not granted", ex)
            }


            updateNextButtonEnabled(connected)
            val pairedDevicesHint = if (connected) stringResource(
                id = R.string.paired_device_connected_hint
            ).format(connectedDeviceName)
            else stringResource(
                id =
                R.string.no_paired_device_connected_hint
            )
            Text(
                text = pairedDevicesHint,
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier.padding(8.dp)
            )
            Button(
                onClick = onPair,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = stringResource(R.string.pair))
            }
        }


    }
}

/////////////////////////////////////////// Previews ////////////////////////////////
@Preview
@Composable
fun CircularProgressPreview() {
    CircularProgress(text = "Loading...")
}


@Preview(showBackground = true)
@Composable
fun TurnBluetoothOnPreview() {
    TurnBluetoothOn(onClick = { /*TODO*/ })
}
