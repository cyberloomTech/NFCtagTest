package com.example.nfcapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun WriteProtectScreen(
    statusText: String,
    inputText: String,
    remainingBlocks: Int,
    lockTag: Boolean,
    onInputTextChanged: (String) -> Unit,
    onLockTagChanged: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {

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
                text = "Remaining 30-char blocks: $remainingBlocks",
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
        lockTag = false,
        onInputTextChanged = {},
        onLockTagChanged = {},
        onNavigateBack = {}
    )
}
