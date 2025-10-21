package com.example.nfcapp

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.nfcapp.ui.navigation.PagerNavigation
import java.nio.charset.Charset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nfcapp.viewmodel.NFCViewModel

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
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFiltersArray: Array<IntentFilter>
    private lateinit var techListsArray: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
//        if (nfcAdapter == null) {
//            Toast.makeText(this, "NFC not supported on this device", Toast.LENGTH_LONG).show()
//            finish()
//            return
//        }
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        val ndefDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        ndefDetected.addCategory(Intent.CATEGORY_DEFAULT)
        intentFiltersArray = arrayOf(ndefDetected)
        techListsArray = arrayOf(
            arrayOf(Ndef::class.java.name),
            arrayOf(NdefFormatable::class.java.name)
        )

        setContent {
            val viewModel: NFCViewModel = viewModel()
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
        setIntent(intent)

            val tag = intent.getParcelableExtra<android.nfc.Tag>(NfcAdapter.EXTRA_TAG)
            if (_inputText.value.isNotEmpty()) {
                val success = writeNdefText(tag, _inputText.value, lockTag)
                _statusTextWrite.value = if (success) {
                    if (lockTag) "Tag written & locked successfully" else "Tag written successfully"
                } else {
                    "Failed to write tag"
                }
            } else {
                val tag = intent.getParcelableExtra<android.nfc.Tag>(NfcAdapter.EXTRA_TAG)
                if (tag != null) {
                    val ndef = Ndef.get(tag)
                    ndef?.connect()
                    val message = ndef?.cachedNdefMessage
                    val records = message?.records
                    if (records != null && records.isNotEmpty()) {
                        val textRecord = String(records[0].payload, Charsets.UTF_8)
                        NFCViewModel.sharedText.value = textRecord
                    }
                    _statusTextRead.value = "Tag Read Successfully"
                } else {
                    _statusTextRead.value = "No NDEF messages found"
                }
                    ndef?.close()
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