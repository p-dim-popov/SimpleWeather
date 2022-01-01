
package com.example.simpleweather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.simpleweather.databinding.FragmentPickupBinding
import com.example.simpleweather.models.AppViewModel
import com.example.simpleweather.network.LocationDefinition
import com.example.simpleweather.network.WeatherApi
import com.example.simpleweather.utils.throttleLatest
import kotlinx.coroutines.launch

class PickupFragment : Fragment() {
    private var binding: FragmentPickupBinding? = null
    private val sharedViewModel: AppViewModel by activityViewModels()
    private val _results = MutableLiveData<List<LocationDefinition>>(listOf())
    val results: LiveData<List<LocationDefinition>> = _results

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentPickupBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        onSearchQueryChanged = throttleLatest(1_000, viewLifecycleOwner.lifecycleScope) {
            when {
                it.isBlank() -> _results.value = listOf()
                else -> viewLifecycleOwner.lifecycleScope.launch {
                    val result = WeatherApi.retrofitService.getLocationsFromQuery(it.toString())
                    _results.value = result
                }
            }
        }

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            pickupFragment = this@PickupFragment
            searchOptionsList.adapter = PossibleLocationsAdapter {
                sharedViewModel.setLocation(AppViewModel.Location.from(it))
                cancel()
            }
        }

        return fragmentBinding.root
    }

    fun cancel() = findNavController().navigate(R.id.action_pickupFragment_to_startFragment)

    lateinit var onSearchQueryChanged: (CharSequence) -> Unit

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}