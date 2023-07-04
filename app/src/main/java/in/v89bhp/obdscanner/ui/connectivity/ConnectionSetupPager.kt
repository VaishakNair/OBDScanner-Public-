package `in`.v89bhp.obdscanner.ui.connectivity

import androidx.activity.ComponentActivity
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import `in`.v89bhp.obdscanner.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConnectionSetupPager(
    backStackEntry: NavBackStackEntry,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConnectionSetupPagerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = backStackEntry
    )
) {
    Box {
        val pageCount = 3
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()

        HorizontalPager(
            pageCount = pageCount,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            userScrollEnabled = false,
            beyondBoundsPageCount = 0
        ) { page ->
            // Page content
            when (page) { // TODO Add new connection screens here:
                0 -> BluetoothIntro()
                1 -> BluetoothConnection(backStackEntry)
                2 -> ConnectionStatus(backStackEntry, navigateBack)
                else -> throw AssertionError("Illegal argument")
            }
        }

        // Step indicator with forward and backward arrows:
        Row(
            Modifier
                .height(80.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {


            IconButton(
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                },
                enabled = pagerState.currentPage != 0
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                    contentDescription = "arrow back",
                )
            }

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

            IconButton(
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                enabled = pagerState.currentPage == 0 || (pagerState.currentPage != pageCount - 1 && viewModel.isNextButtonEnabled)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                    contentDescription = "arrow forward"
                )
            }
        }
    }
}