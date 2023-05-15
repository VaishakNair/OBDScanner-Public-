package `in`.v89bhp.obdscanner.ui.connectivity

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.v89bhp.obdscanner.R

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun ConnectionSetupPager() {
    Box {
        // [START android_compose_pager_indicator]
        val pageCount = 3
        val pagerState = rememberPagerState()

        HorizontalPager(
            pageCount = pageCount,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Page content
            when (page) { // TODO Add new connection screens here:
                0 -> BluetoothIntro()
                else -> throw AssertionError("Illegal argument")
            }
        }
        Row(
            Modifier
                .height(80.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                contentDescription = "arrow back",

                )
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .size(70.dp)

            ) {
                Text(
                    text = (pagerState.currentPage + 1).toString(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(6.dp),
                    style = MaterialTheme.typography.displaySmall
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                contentDescription = "arrow forward"
            )

        }
        // [END android_compose_pager_indicator]
    }
}