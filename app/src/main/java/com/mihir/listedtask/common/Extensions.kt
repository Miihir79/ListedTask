package com.mihir.listedtask.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.AssetManager
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

private lateinit var toast: Toast

// This file stores all the extension functions to make code concise
/**
 * Function used to show toast message: [message] Toast.LENGTH_LONG
 */
fun Context.showToastMessage(message: String) {
    if (::toast.isInitialized) {
        toast.cancel()
    }
    toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.show()
}


/**
 * Function used to copy text to clipboard
 */
fun String.copyToClipboard(context: Context) {
    val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = ClipData.newPlainText("link", this)
    clipBoard.setPrimaryClip(text)
}