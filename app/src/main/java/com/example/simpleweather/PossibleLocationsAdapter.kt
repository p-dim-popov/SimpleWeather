package com.example.simpleweather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweather.databinding.SuggestionListViewItemBinding
import com.example.simpleweather.network.LocationDefinition

class PossibleLocationsAdapter: ListAdapter<LocationDefinition, PossibleLocationsAdapter.PossibleLocationViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<LocationDefinition>() {
        override fun areItemsTheSame(oldItem: LocationDefinition, newItem: LocationDefinition): Boolean = oldItem.name == newItem.name && oldItem.country == newItem.country
        override fun areContentsTheSame(oldItem: LocationDefinition, newItem: LocationDefinition): Boolean = areItemsTheSame(oldItem, newItem)
    }

    class PossibleLocationViewHolder(private var binding: SuggestionListViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(location: LocationDefinition) {
            binding.location = location
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PossibleLocationViewHolder {
        val layout = SuggestionListViewItemBinding.inflate(LayoutInflater.from(parent.context))
        return PossibleLocationViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PossibleLocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }
}