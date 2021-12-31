package com.example.simpleweather

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweather.network.LocationDefinition
import java.lang.Exception

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<LocationDefinition>?) {
    when (val adapter = recyclerView.adapter) {
        is PossibleLocationsAdapter -> adapter.submitList(data)
        is SearchLocationAdapter -> adapter.submitList(data)
    }
}
