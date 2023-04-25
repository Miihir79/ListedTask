package com.mihir.listedtask.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mihir.listedtask.data.model.InfoTileData
import com.mihir.listedtask.databinding.CardInfoBinding

class AdapterInfoTile : ListAdapter<InfoTileData, AdapterInfoTile.ViewHolder>(ItemCallback) {

    object ItemCallback : DiffUtil.ItemCallback<InfoTileData>() {
        override fun areItemsTheSame(oldItem: InfoTileData, newItem: InfoTileData): Boolean =
            oldItem.resID == newItem.resID

        override fun areContentsTheSame(oldItem: InfoTileData, newItem: InfoTileData): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    var infoItemData: List<InfoTileData> = emptyList()
        set(value) {
            field = value
            submitList(field)
        }


    inner class ViewHolder(private val binding: CardInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InfoTileData) {
            with(binding) {
                imageView.setImageResource(item.resID)
                textViewTitle.text = item.titleText
                textViewType.text = item.subText
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(CardInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}