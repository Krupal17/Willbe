package com.spycone.next.willbe.file

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spycone.next.willbe.databinding.ItemDuplicateFileBinding
import java.io.File

class DuplicateFilesAdapter(var files: ArrayList<File>) :
    RecyclerView.Adapter<DuplicateFilesAdapter.DuplicateFileViewHolder>() {
    class DuplicateFileViewHolder(private val binding: ItemDuplicateFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.file = file
            binding.fileSize.text = getFileSize(file)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuplicateFileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDuplicateFileBinding.inflate(inflater, parent, false)


        return DuplicateFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DuplicateFileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(file)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    companion object {

        fun getFileSize(file: File): String {
            val length = file.length()
            return when {
                length < 1024L -> "$length B"
                length < 1024L * 1024L -> "${String.format("%.2f", length.toDouble() / 1024)} KB"
                length < 1024L * 1024L * 1024L -> "${
                    String.format(
                        "%.2f",
                        length.toDouble() / (1024 * 1024)
                    )
                } MB"

                else -> "${String.format("%.2f", length.toDouble() / (1024 * 1024 * 1024))} GB"
            }
        }
    }


}
