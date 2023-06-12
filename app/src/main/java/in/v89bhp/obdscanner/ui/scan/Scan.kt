package `in`.v89bhp.obdscanner.ui.scan

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidViewBinding
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ScanOtherFragmentLayoutBinding
import `in`.v89bhp.obdscanner.databinding.ScanTroubleCodesFragmentLayoutBinding
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Scan(modifier: Modifier = Modifier) {

    val titles =
        listOf(stringResource(id = R.string.trouble_codes), stringResource(id = R.string.other))
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
            state = pagerState
        ) { page ->
            if (page == 0) { // Tab 1
                AndroidViewBinding(

                    factory = ScanTroubleCodesFragmentLayoutBinding::inflate
                ) {

                }
            } else { // Tab 2
                AndroidViewBinding(

                    factory = ScanOtherFragmentLayoutBinding::inflate
                ) {

                }
            }
        }
    }


    // Clear trouble codes dialog:
    if (ScanUiState.showClearTroubleCodesDialog) {
        AlertDialog(onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    ScanUiState.clearTroubleCodes()
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    ScanUiState.showClearTroubleCodesDialog = false
                }) { Text(stringResource(R.string.cancel)) }
            },
            title = {},
            text = {
                Text(stringResource(R.string.clear_dtc_confirmation_message))
            })
    }
}