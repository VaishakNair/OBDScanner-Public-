package `in`.v89bhp.obdscanner.ui.connectivity

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator()
        Text(text = text,
        modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun PairedDevices(pairedDevices: List<BluetoothDevicz>, modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(16.dp)
    ) {
      items(pairedDevices, key = {it.address}) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = it.name,
            )
            Text(text = "Connected",
            modifier = Modifier.padding(top = 8.dp))
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
    PairedDevices(listOf(BluetoothDevicz("1", "boAt"), BluetoothDevicz("2", "Jabra")))
}

data class BluetoothDevicz(val address: String, val name: String)