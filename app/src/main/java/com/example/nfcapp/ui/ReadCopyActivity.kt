package com.example.nfcapp.ui

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.nio.charset.Charset

class ReadCopyActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    // State variables to display on UI
    private val _statusText = mutableStateOf("Ready to scan NFC tag")
    private val _nfcText = mutableStateOf("(No tag read yet)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not supported on this device", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Set Compose content
        setContent {
            ReadCopyScreen()
        }
    }

    // Enable Foreground Dispatch to catch NFC intents while app is in foreground
    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        val techList = arrayOf<Array<String>>() // accept all tag types
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, techList)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    // Handle NFC tag detection
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            val msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (msgs != null) {
                val record = (msgs[0] as NdefMessage).records[0]
                val text = readTextFromNdefRecord(record)
                _nfcText.value = text
                _statusText.value = "Tag Read Successfully"
            } else {
                _statusText.value = "No NDEF messages found"
            }
        }
    }

    // Compose UI for the screen
    @Composable
    fun ReadCopyScreen() {
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
                    text = _statusText.value,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = _nfcText.value,
                    fontSize = 22.sp,
                    color = Color.Yellow,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Function to decode NDEF Text record
    private fun readTextFromNdefRecord(record: NdefRecord): String {
        val payload = record.payload
        val textEncoding =
            if ((payload[0].toInt() and 128) == 0) Charset.forName("UTF-8")
            else Charset.forName("UTF-16")
        val languageCodeLength = payload[0].toInt() and 63
        return String(payload, languageCodeLength + 1,
            payload.size - languageCodeLength - 1, textEncoding)
    }

    @Preview
    @Composable
    fun ReadPreview() {
        ReadCopyScreen()
    }

}

