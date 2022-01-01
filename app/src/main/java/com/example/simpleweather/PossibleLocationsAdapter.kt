package com.example.simpleweather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweather.databinding.SearchListViewItemBinding
import com.example.simpleweather.network.LocationDefinition

typealias OnClickLocationHandler = (LocationDefinition) -> Unit

class PossibleLocationsAdapter(private val onClickLocation: OnClickLocationHandler): ListAdapter<LocationDefinition, PossibleLocationsAdapter.PossibleLocationViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<LocationDefinition>() {
        override fun areItemsTheSame(oldItem: LocationDefinition, newItem: LocationDefinition): Boolean = oldItem.name == newItem.name && oldItem.country == newItem.country
        override fun areContentsTheSame(oldItem: LocationDefinition, newItem: LocationDefinition): Boolean = areItemsTheSame(oldItem, newItem)
    }

    class PossibleLocationViewHolder(private var binding: SearchListViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(location: LocationDefinition, onClickLocation: OnClickLocationHandler) {
            binding.location = location
            binding.locationOption.setOnClickListener { onClickLocation(location) }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PossibleLocationViewHolder {
        val layout = SearchListViewItemBinding.inflate(LayoutInflater.from(parent.context))
        return PossibleLocationViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PossibleLocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location, onClickLocation)
    }
}