package com.example.nfcapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReadCopyScreen(
    statusText: String,
    nfcText: String,
    onNavigateToWrite: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
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
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = nfcText,
                fontSize = 22.sp,
                color = Color.Yellow,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToWrite,
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
        nfcText = "(No tag read yet)",
        onNavigateToWrite = {}
    )
}
