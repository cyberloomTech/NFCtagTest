package com.example.nfcapp.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.nfcapp.ui.ReadCopyScreen
import com.example.nfcapp.ui.WriteProtectScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerNavigation(
    statusTextRead: String,
    nfcText: String,
    statusTextWrite: String,
    inputText: String,
    remainingBlocks: Int,
    lockTag: Boolean,
    onInputTextChanged: (String) -> Unit,
    onLockTagChanged: (Boolean) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("Read/Copy") }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("Write/Protect") }
            )
        }
        HorizontalPager(state = pagerState) {
            when (it) {
                0 -> ReadCopyScreen(
                    statusText = statusTextRead,
                    nfcText = nfcText,
                    onNavigateToWrite = { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
                )
                1 -> WriteProtectScreen(
                    statusText = statusTextWrite,
                    inputText = inputText,
                    remainingBlocks = remainingBlocks,
                    lockTag = lockTag,
                    onInputTextChanged = onInputTextChanged,
                    onLockTagChanged = onLockTagChanged,
                    onNavigateBack = { coroutineScope.launch { pagerState.animateScrollToPage(0) } }
                )
            }
        }
    }
}