package `in`.v89bhp.obdscanner.ui.settings

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import com.github.anastr.speedviewlib.DeluxeSpeedView
import com.github.anastr.speedviewlib.RaySpeedometer
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.TubeSpeedometer
import `in`.v89bhp.obdscanner.R

@Composable
fun ComposablePreference(
    title: String,
    summary: String,
  modifier: Modifier = Modifier,
    onClick: () -> Unit
) {


    Box(modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .clickable { onClick() }) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 50.dp, top = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 16.sp
            )
            Text(
                text = summary,
                fontSize = 14.sp
            )
        }
    }

}



