package com.example.simpleweather

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweather.network.LocationDefinition

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<LocationDefinition>?) {
    val adapter = recyclerView.adapter as PossibleLocationsAdapter
    adapter.submitList(data)
}
