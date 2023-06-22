package `in`.v89bhp.obdscanner.ui.scan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import `in`.v89bhp.obdscanner.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScanContainer(
    onNavigateTo: (route: String) -> Unit,
    modifier: Modifier = Modifier) {
    val titles = listOf(stringResource(R.string.trouble_codes), stringResource(R.string.other))
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    Column(modifier = modifier) {
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
            userScrollEnabled = false
        ) { page ->
            if (page == 0) { // Tab 1
                ScanTroubleCodes(onNavigateTo = onNavigateTo)
            } else { // Tab 2
                ScanOther()
            }
        }
    }
}