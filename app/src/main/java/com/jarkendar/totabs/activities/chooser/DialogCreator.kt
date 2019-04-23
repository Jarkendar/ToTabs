package com.jarkendar.totabs.activities.chooser

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context

class DialogCreator(private val context: Context) {

    public fun createDialog(title: String, text: String, positiveButtonText: String): Dialog {
        return AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton(positiveButtonText) { _, _ -> }
                .create()
    }
}