
package com.example.simpleweather

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.simpleweather.databinding.FragmentFlavorBinding
import com.example.simpleweather.models.AppViewModel
import com.example.simpleweather.models.AppViewModelFactory
import com.example.simpleweather.utils.Constants
import com.example.simpleweather.utils.LocationListenerWithLocationManager
import com.example.simpleweather.utils.arePermissionsGranted
import com.example.simpleweather.utils.requestLocationUpdates
import kotlinx.coroutines.launch


class FlavorFragment : Fragment(), LocationListenerWithLocationManager {
    override lateinit var locationManager: LocationManager
    private var binding: FragmentFlavorBinding? = null
    private val sharedViewModel: AppViewModel by activityViewModels { AppViewModelFactory(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locationManager = getSystemService(requireContext(), LocationManager::class.java) as LocationManager
        if (sharedViewModel.possibleLocations.value.isNullOrEmpty()) {
            if (requireActivity().arePermissionsGranted(Constants.RequiredPermissions_location)) this.requestLocationUpdates()
            else ActivityCompat.requestPermissions(requireActivity(), Constants.RequiredPermissions_location, 1)
        }

        val fragmentBinding = FragmentFlavorBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            flavorFragment = this@FlavorFragment
            possibleOptionsList.adapter = PossibleLocationsAdapter {
                sharedViewModel.setLocation(AppViewModel.Location.from(it))
                cancel()
            }
        }

        return fragmentBinding.root
    }

    fun goToManualInput() = findNavController().navigate(R.id.action_flavorFragment_to_pickupFragment)

    fun cancel() = findNavController().navigate(R.id.action_flavorFragment_to_startFragment)

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onLocationChanged(location: Location) {
        with(location) {
            sharedViewModel
                .updatePossibleLocations(longitude.toString(), latitude.toString())
                .invokeOnCompletion {
                    Toast.makeText(requireContext(), "Possible locations for: \n lon - $longitude\n lat - $latitude", Toast.LENGTH_LONG)
                        .show()
                }
        }

        locationManager.removeUpdates(this)
    }

    fun refreshPossibleLocations() = requestLocationUpdates()
}