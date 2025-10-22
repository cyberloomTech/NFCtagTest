package com.example.nfcapp.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nfcapp.viewmodel.NFCViewModel


@Composable
fun WriteProtectScreen(
    statusText: String,
    inputText: String,
    remainingBlocks: Int,
    writtenStrLength: Int,
    lockTag: Boolean,
    onInputTextChanged: (String) -> Unit,
    onLockTagChanged: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val isWriteMode by NFCViewModel.isWriteMode

    // Animation for pulse when in write mode
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isWriteMode)
                    Color(0xFF3DDC84).copy(alpha = pulseAlpha * 0.2f) // Android green pulse
                else
                    MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status text
            Text(
                text = statusText,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )

            // Input text field
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                label = { Text("Enter text to write") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Remaining 30-char blocks
            Text(
                text = "$writtenStrLength / Remaining 30-char blocks: $remainingBlocks",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // LOCK TAG CHECKBOX
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Checkbox(
                    checked = lockTag,
                    onCheckedChange = onLockTagChanged,
                    colors = CheckboxDefaults.colors(checkedColor = Color.Cyan)
                )
                Text(
                    text = "Lock tag after writing",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    NFCViewModel.textToWrite.value = inputText
                    NFCViewModel.lockTagAfterWrite.value = lockTag
                    NFCViewModel.isWriteMode.value = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text("Ready to Write — Tap NFC Tag", fontSize = 18.sp)
            }

            Spacer(Modifier.height(24.dp))

            // Instruction
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text("Back to Read/Copy", fontSize = 18.sp)
            }
        }
    }
}

@Preview
@Composable
fun WriteProtectScreenPreview() {
    WriteProtectScreen(
        statusText = "Ready to write NFC tag",
        inputText = "",
        remainingBlocks = 0,
        writtenStrLength = 0,
        lockTag = false,
        onInputTextChanged = {},
        onLockTagChanged = {},
        onNavigateBack = {}
    )
}
