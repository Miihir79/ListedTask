package com.mihir.listedtask.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mihir.listedtask.common.copyToClipboard
import com.mihir.listedtask.common.showToastMessage
import com.mihir.listedtask.data.model.LinkItem
import com.mihir.listedtask.databinding.CardInfoBinding
import com.mihir.listedtask.databinding.ItemLinkBinding
import java.text.SimpleDateFormat
import kotlin.coroutines.coroutineContext

class AdapterLink : ListAdapter<LinkItem, AdapterLink.ViewHolder>(ItemCallback) {

    object ItemCallback : DiffUtil.ItemCallback<LinkItem>() {
        override fun areItemsTheSame(oldItem: LinkItem, newItem: LinkItem): Boolean =
            oldItem.url_id == newItem.url_id

        override fun areContentsTheSame(oldItem: LinkItem, newItem: LinkItem): Boolean =
            oldItem == newItem
    }

    // use the same for top and recent links
    var linkItemData: List<LinkItem> = emptyList()
        set(value) {
            field = value
            submitList(value)
        }

    inner class ViewHolder(private val binding: ItemLinkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LinkItem) {
            with(binding) {
                textViewCount.text = item.total_clicks.toString()
                textViewLinkName.text = item.title
                textViewLink.text = item.smart_link
                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(item.created_at)
                val formattedDate = date?.let { SimpleDateFormat("dd-MMM-yyyy").format(it) }
                textViewDate.text = formattedDate
                imageViewCopy.setOnClickListener {
                    item.smart_link.copyToClipboard(binding.imageViewCopy.context)
                    binding.imageViewCopy.context.showToastMessage("Copied to Clipboard")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}