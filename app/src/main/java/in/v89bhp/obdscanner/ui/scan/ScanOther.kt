package `in`.v89bhp.obdscanner.ui.scan

import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ScanOtherGeneralCardBinding
import `in`.v89bhp.obdscanner.databinding.ScanOtherOxygenSensorCardBinding
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.ui.connectivity.CircularProgress
import `in`.v89bhp.obdscanner.ui.connectivity.ErrorCard
import kotlinx.coroutines.launch

@Composable
fun ScanOther(
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier,
    viewModel: ScanOtherViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    ),
    lifecycleOwner: LifecycleOwner = backStackEntry
) {

    Box(modifier = modifier.fillMaxSize()) {
        if (viewModel.fetching) {
            CircularProgress(text = stringResource(R.string.fetching))
        } else if (viewModel.isError) {
            ErrorCard(
                errorMessage = viewModel.errorMessage!!,
                onClick = { viewModel.loadOtherData() })
        } else if (viewModel.scanCompleted) {
            ScanOtherCompleted(viewModel)
        } else {
            StartScan(onClick = { viewModel.loadOtherData() })
        }
    }



    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.dismissPopupWindow()
                }

                else -> {}
            }
        }
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun ScanOtherCompleted(
    viewModel: ScanOtherViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            GeneralCard(viewModel)
        }

        item {
            OxygenSensorCard(viewModel)
        }


    }
}

@Composable
fun OxygenSensorCard(
    viewModel: ScanOtherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AndroidViewBinding(factory = ScanOtherOxygenSensorCardBinding::inflate) {

            fun getChildTextView(key: String, value: String): TextView =
                TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        leftMargin = resources.getDimension(R.dimen.margin).toInt()
                        bottomMargin = leftMargin
                    }

                    text = context.getString(R.string.key_colon_value, key, value)
                }

            oxygenSensorsLayout.apply {
                for (sensor in viewModel.oxygenSensorList) {
                    addView(
                        getChildTextView(
                            sensor.substringBefore(':').trim(),
                            sensor.substringAfter(':').trim()
                        )
                    )
                }
                for ((key, value) in viewModel.otherDataMap.entries) {
                    if (key.contains(
                            context.getString(R.string.oxygen),
                            true
                        )
                    ) {// Oxygen sensor-related data
                        addView(getChildTextView(key, value))
                    }
                }
            }

            oxygenSensorTypeTextView.text = viewModel.oxygenSensorType
            oxygenSensorTypeInfoView.setOnClickListener {
                // Dismiss any existing popup window:
                viewModel.dismissPopupWindow()
                viewModel.popupWindow = Utilities.showPopupWindow(
                    LayoutInflater.from(context),
                    it,
                    context.getString(R.string.oxygen_sensor_types_title),
                    context.getString(R.string.oxygen_sensor_types)
                )
            }
        }
    }
}

@Composable
fun GeneralCard(
    viewModel: ScanOtherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AndroidViewBinding(factory = ScanOtherGeneralCardBinding::inflate) {
            generalLayout.apply {
                for ((key, value) in viewModel.otherDataMap.entries) {
                    if (key.contains(context.getString(R.string.oxygen), true)
                            .not()
                    ) {// Not related to oxygen sensors.
                        // O2 sensor data is displayed separately.
                        addView(TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                leftMargin = resources.getDimension(R.dimen.margin).toInt()
                                bottomMargin = leftMargin
                            }

                            text = context.getString(R.string.key_colon_value, key, value)
                        })
                    }
                }
            }
        }
    }

}