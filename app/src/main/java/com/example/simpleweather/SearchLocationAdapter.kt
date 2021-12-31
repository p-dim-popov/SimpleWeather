package com.example.simpleweather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweather.databinding.SearchListViewItemBinding
import com.example.simpleweather.network.LocationDefinition

class SearchLocationAdapter: ListAdapter<LocationDefinition, SearchLocationAdapter.SearchLocationViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<LocationDefinition>() {
        override fun areItemsTheSame(oldItem: LocationDefinition, newItem: LocationDefinition): Boolean = oldItem.name == newItem.name && oldItem.country == newItem.country
        override fun areContentsTheSame(oldItem: LocationDefinition, newItem: LocationDefinition): Boolean = areItemsTheSame(oldItem, newItem)
    }

    class SearchLocationViewHolder(private var binding: SearchListViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(location: LocationDefinition) {
            binding.location = location
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationViewHolder {
        val layout = SearchListViewItemBinding.inflate(LayoutInflater.from(parent.context))
        return SearchLocationViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SearchLocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }
}