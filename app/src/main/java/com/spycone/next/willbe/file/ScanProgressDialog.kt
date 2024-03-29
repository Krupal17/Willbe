package com.spycone.next.willbe.file

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import com.spycone.next.willbe.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScanProgressDialog(var activity: Activity) : Dialog(activity) {

    private lateinit var startTimeTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var txtProcess: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_scan_progress)

        startTimeTextView = findViewById(R.id.startTimeText)
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        txtProcess = findViewById(R.id.txtProcess)

        var i = 0
        CoroutineScope(Dispatchers.IO).launch {
            recurciveLoader(i)
        }

        val startTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        startTimeTextView.text = "Scan Start Time: $startTime"
    }

    private suspend fun recurciveLoader(i: Int) {
        delay(300)
        if (i == 0) {
            progressText.texter = "Scanning."
            recurciveLoader(1)
        } else if (i == 1) {
            progressText.texter = "Scanning.."
            recurciveLoader(2)
        } else {
            progressText.texter = "Scanning..."
            recurciveLoader(0)
        }
    }

    fun setProgress(progress: Int) {
        progressBar.progress = progress
        txtProcess.text = "$progress%"
    }

    var TextView.texter: String
        get() {
            return this.text.toString()
        }
        set(value) {
            activity.runOnUiThread {
                this.text = value
            }
        }
}
