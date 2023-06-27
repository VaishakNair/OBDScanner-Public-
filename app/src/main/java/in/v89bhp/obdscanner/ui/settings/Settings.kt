package `in`.v89bhp.obdscanner.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.preference.PreferenceManager
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import com.github.anastr.speedviewlib.DeluxeSpeedView
import com.github.anastr.speedviewlib.RaySpeedometer
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.TubeSpeedometer
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    onNavigateTo: (route: String) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sharedPrefs =
        PreferenceManager.getDefaultSharedPreferences(LocalContext.current)

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings))
                },
                actions = {

                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })

        }) { contentPadding ->
        Column(modifier = modifier.padding(contentPadding)) {

            val gaugeName = getGaugeName(
                gaugeType = sharedPrefs.getString(
                    "gaugeType",
                    AwesomeSpeedometer::class.java.name
                )!!
            )
            ComposablePreference(
                title = "Gauge",
                summary = gaugeName,
                onClick = {
                    onNavigateTo(Screen.GaugeTypePicker.route)
                })

            ComposablePreferenceCategory(title = "Units") {

                ComposableListPreference(
                    sharedPrefs = sharedPrefs,
                    title = "Distance",
                    key = "distance",
                    defaultValue = "km",
                    entries = listOf("Kilometre", "Mile"),
                    entryValues = listOf("km", "m")
                )

                ComposableListPreference(
                    sharedPrefs = sharedPrefs,
                    title = "Temperature",
                    key = "temperature",
                    defaultValue = "c",
                    entries = listOf("Celsius", "Fahrenheit"),
                    entryValues = listOf("c", "f")
                )

                ComposableListPreference(
                    sharedPrefs = sharedPrefs,
                    title = "Pressure",
                    key = "pressure",
                    defaultValue = "p",
                    entries = listOf("PSI", "Bar"),
                    entryValues = listOf("p", "b")
                )
            }

        }
    }


}

@Composable
fun getGaugeName(gaugeType: String) =
    when (gaugeType) {
        AwesomeSpeedometer::class.java.name -> stringResource(R.string.awesome_speedometer)
        TubeSpeedometer::class.java.name -> stringResource(R.string.tube_speedometer)
        SpeedView::class.java.name -> stringResource(R.string.speed_view)
        DeluxeSpeedView::class.java.name -> stringResource(R.string.deluxe_speed_view)
        RaySpeedometer::class.java.name -> stringResource(R.string.ray_speedometer)
        else -> stringResource(R.string.awesome_speedometer)
    }