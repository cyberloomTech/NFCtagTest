package com.example.nfcapp

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.nfcapp.ui.navigation.PagerNavigation
import java.nio.charset.Charset

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private val maxCapacity = 888 // NTAG216 max capacity

    // State for ReadCopyScreen
    private val _statusTextRead = mutableStateOf("Ready to scan NFC tag")
    private val _nfcText = mutableStateOf("(No tag read yet)")

    // State for WriteProtectScreen
    private val _statusTextWrite = mutableStateOf("Ready to write NFC tag")
    private val _inputText = mutableStateOf("")
    private val _remainingBlocks = mutableStateOf(0)
    private var lockTag by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
//        if (nfcAdapter == null) {
//            Toast.makeText(this, "NFC not supported on this device", Toast.LENGTH_LONG).show()
//            finish()
//            return
//        }

        setContent {
            PagerNavigation(
                statusTextRead = _statusTextRead.value,
                nfcText = _nfcText.value,
                statusTextWrite = _statusTextWrite.value,
                inputText = _inputText.value,
                remainingBlocks = _remainingBlocks.value,
                lockTag = lockTag,
                onInputTextChanged = { newText ->
                    _inputText.value = newText
                    _remainingBlocks.value = (maxCapacity - newText.length) / 30
                },
                onLockTagChanged = { lockTag = it }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED), IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED))
        val techList = arrayOf(arrayOf<String>(Ndef::class.java.name))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, techList)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            if (_inputText.value.isNotEmpty()) {
                val success = writeNdefText(tag, _inputText.value, lockTag)
                _statusTextWrite.value = if (success) {
                    if (lockTag) "Tag written & locked successfully" else "Tag written successfully"
                } else {
                    "Failed to write tag"
                }
            } else {
                val msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                if (msgs != null) {
                    val record = (msgs[0] as NdefMessage).records[0]
                    val text = readTextFromNdefRecord(record)
                    _nfcText.value = text
                    _statusTextRead.value = "Tag Read Successfully"
                } else {
                    _statusTextRead.value = "No NDEF messages found"
                }
            }
        }
    }

    private fun writeNdefText(tag: Tag, text: String, lockTag: Boolean = false): Boolean {
        return try {
            val ndef = Ndef.get(tag)
            ndef.connect()
            val record = NdefRecord.createTextRecord("en", text)
            val message = NdefMessage(arrayOf(record))
            ndef.writeNdefMessage(message)
            if (lockTag && ndef.isWritable) {
                ndef.makeReadOnly()
            }
            ndef.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun readTextFromNdefRecord(record: NdefRecord): String {
        val payload = record.payload
        val textEncoding =
            if ((payload[0].toInt() and 128) == 0) Charset.forName("UTF-8")
            else Charset.forName("UTF-16")
        val languageCodeLength = payload[0].toInt() and 63
        return String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)
    }
}