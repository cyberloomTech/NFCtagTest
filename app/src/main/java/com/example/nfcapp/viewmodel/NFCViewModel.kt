package com.example.nfcapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NFCViewModel : ViewModel() {
    companion object {
        val sharedText = mutableStateOf("No tag scanned yet.")
    }
}
