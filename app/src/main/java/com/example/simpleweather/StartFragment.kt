
package com.example.simpleweather

import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.simpleweather.databinding.FragmentStartBinding
import com.example.simpleweather.models.LocationWeatherViewModel
import com.example.simpleweather.utils.Constants
import com.example.simpleweather.utils.LocationListenerWithLocationManager
import com.example.simpleweather.utils.arePermissionsGranted
import com.example.simpleweather.utils.requestLocationUpdates
import kotlin.math.abs

class StartFragment : Fragment(), LocationListenerWithLocationManager {
    override lateinit var locationManager: LocationManager

    private var binding: FragmentStartBinding? = null
    private val sharedViewModel: LocationWeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locationManager = ContextCompat.getSystemService(
            requireContext(),
            LocationManager::class.java
        ) as LocationManager

        val fragmentBinding = FragmentStartBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        val gesture = GestureDetector(requireContext(), object : SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val SWIPE_MIN_DISTANCE = 120
                    val SWIPE_MAX_OFF_PATH = 250
                    val SWIPE_THRESHOLD_VELOCITY = 200
                    try {
                        if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false

                        if (e1.x - e2.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            navigateToChangeLocationFragment()
                        }
                    } catch (e: Exception) {
                        // nothing
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })

        binding?.root?.setOnTouchListener { v, event ->
            v.performClick()
            gesture.onTouchEvent(event)
        }

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Comment this to have persistence
//        sharedPref.edit().clear().commit()
        when(val locationName = sharedPref.getString(Constants.SharedPreferences_locationName, null)) {
            null -> requestLocation()
            LocationWeatherViewModel.initialState.locationName.value -> requestLocation()
            else -> sharedViewModel.setLocationName(locationName)
        }

        return fragmentBinding.root
    }

    private fun requestLocation() =
        if (requireActivity().arePermissionsGranted(Constants.RequiredPermissions_location)) this.requestLocationUpdates()
        else ActivityCompat.requestPermissions(
            requireActivity(),
            Constants.RequiredPermissions_location,
            1
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            startFragment = this@StartFragment
        }
    }

    fun navigateToChangeLocationFragment() = findNavController().navigate(R.id.action_startFragment_to_flavorFragment)

    fun toggleNotifications() {
        val (newState, willReceive) = when(sharedViewModel.notifications.value) {
            true -> listOf("off", "stop")
            else -> listOf("on", "start")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Are you sure that you want to turn notifications $newState?")
            .setMessage("You will $willReceive receiving notifications with current weather every 1 hour")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ -> sharedViewModel.toggleNotifications() }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onLocationChanged(location: Location) {
        if (sharedViewModel.locationName.value == LocationWeatherViewModel.initialState.locationName.value) {
            sharedViewModel.autoSetupLocationName(
                location.longitude.toString(),
                location.latitude.toString(),
            )
        }
    }
}