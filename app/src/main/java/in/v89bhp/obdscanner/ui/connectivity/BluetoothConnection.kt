package `in`.v89bhp.obdscanner.ui.connectivity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper

@Composable
fun BluetoothConnection(
    modifier: Modifier = Modifier,
    viewModel: BluetoothConnectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    CircularProgress(text = "Loading...")
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

@SuppressLint("MissingPermission")
@Composable
fun PairedDevices(pairedDevices: List<BluetoothDevice>, modifier: Modifier = Modifier) {
    val bluetoothSocket = BluetoothHelper.socket
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(16.dp)
    ) {
        items(pairedDevices, key = { it.address }) { bluetoothDevice ->
            val connected =
                bluetoothSocket?.let { it.isConnected && it.remoteDevice.name == bluetoothDevice!!.name }
                    ?: false
            Column(modifier = Modifier.fillMaxWidth()) {
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
    }
}


@Preview
@Composable
fun CircularProgressPreview() {
    CircularProgress(text = "Loading...")
}

@Preview(showBackground = true)
@Composable
fun PairedDevicesPreview() {
//    PairedDevices(listOf(BluetoothDevicz("1", "boAt"), BluetoothDevicz("2", "Jabra")))
}

data class BluetoothDevicz(val address: String, val name: String)