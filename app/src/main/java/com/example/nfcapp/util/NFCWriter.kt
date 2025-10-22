package com.example.nfcapp.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.VibrationEffect
import android.os.Vibrator
import java.nio.charset.Charset
import java.util.*


object NFCWriter {

    fun writeNdefText(context: Context, tag: Tag, text: String, lockAfterWrite: Boolean = false): Boolean {
        try {
            val lang = Locale.getDefault().language
            val langBytes = lang.toByteArray(Charset.forName("US-ASCII"))
            val textBytes = text.toByteArray(Charset.forName("UTF-8"))
            val payload = ByteArray(1 + langBytes.size + textBytes.size)
            payload[0] = langBytes.size.toByte()
            System.arraycopy(langBytes, 0, payload, 1, langBytes.size)
            System.arraycopy(textBytes, 0, payload, 1 + langBytes.size, textBytes.size)

            val record = NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT,
                ByteArray(0),
                payload
            )

            val ndefMessage = NdefMessage(arrayOf(record))

            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) return false

                ndef.writeNdefMessage(ndefMessage)

                if (lockAfterWrite) {
                    ndef.makeReadOnly()
                }

                ndef.close()
                playSuccessFeedback(context)
                return true
            } else {
                val format = NdefFormatable.get(tag)
                format?.connect()
                format?.format(ndefMessage)
                format?.close()
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
    private fun playSuccessFeedback(context: Context) {
        // Vibration
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(
            VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
        )

        // Tone
        val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
    }
}
