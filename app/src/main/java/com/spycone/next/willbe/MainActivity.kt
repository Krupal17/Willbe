package com.spycone.next.willbe

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.spycone.next.willbe.databinding.ActivityMainBinding
import com.spycone.next.willbe.file.DuplicateFilesAdapter
import com.spycone.next.willbe.file.ScanProgressDialog
import com.spycone.next.willbe.file.findDuplicateMediaFiles
import java.io.File

class MainActivity : AppCompatActivity() {
    var files = ArrayList<File>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DuplicateFilesAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.scanButton.setOnClickListener {
            Log.e("DuplicateFiles", "onCreate: ")
            scanFiles()
        }

        adapter = DuplicateFilesAdapter(files)
        binding.recyclerView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun scanFiles() {
        val progressDialog = ScanProgressDialog(this)
//        progressDialog.setTitle("Scanning Files")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val handler = Handler(Looper.myLooper()!!)

        findDuplicateMediaFiles(this, { per ->
            runOnUiThread {
                Log.e("DuplicateFiles->", "scanFiles: $per")
                handler.post {
                    progressDialog.setProgress(per)
                }
            }
        }, {
            runOnUiThread {
                progressDialog.dismiss()
                adapter.files.clear()
                adapter.files.addAll(it)
                adapter.notifyDataSetChanged()
                binding.scanButton.visibility=View.GONE
            }
            Log.e("DuplicateFiles->", "scanFiles: $it")

        })


    }

}