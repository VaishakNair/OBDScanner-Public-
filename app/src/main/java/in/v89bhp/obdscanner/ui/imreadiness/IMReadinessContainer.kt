package `in`.v89bhp.obdscanner.ui.imreadiness

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavBackStackEntry
import `in`.v89bhp.obdscanner.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IMReadinessContainer(
    backStackEntry: NavBackStackEntry,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titles = listOf(stringResource(R.string.since_dtc_cleared), stringResource(R.string.this_driving_cycle))
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()


    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.im_readiness))
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
            TabRow(selectedTabIndex = pagerState.currentPage) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge
                            )


                        }
                    )
                }
            }

            HorizontalPager(
                pageCount = titles.size,
                state = pagerState,
                userScrollEnabled = true,
            ) { page ->
                if (page == 0) { // Tab 1
                    IMReadinessSinceDtcCleared(backStackEntry = backStackEntry)
                } else { // Tab 2
                    IMReadinessDrivingCycle(backStackEntry = backStackEntry)
                }
            }
        }
    }
}