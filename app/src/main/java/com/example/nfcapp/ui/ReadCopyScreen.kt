package com.example.nfcapp.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ReadCopyScreen(
    statusText: String,
    nfcText: String,
    onNavigateToWrite: () -> Unit
) {

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statusText,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Display NFC Text
            Text(
                text = nfcText,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        Toast.makeText(context, "Tag simulated", Toast.LENGTH_SHORT).show()
                    }
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Copy Button
            Button(
                onClick = {
                    if (nfcText.isNotBlank()) {
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("NFC Text", nfcText)
                        clipboard.setPrimaryClip(clip)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Copy Text", fontSize = 18.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNavigateToWrite()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Go to Write & Protect", fontSize = 18.sp)
            }
        }
    }
}

@Preview
@Composable
fun ReadPreview() {
    ReadCopyScreen(
        statusText = "Ready to scan NFC tag",
        nfcText = "(No scanned text)",
        onNavigateToWrite = {}
    )
}
