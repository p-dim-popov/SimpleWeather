package com.example.simpleweather

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.simpleweather.models.LocationWeatherViewModel
import com.example.simpleweather.utils.Constants
import com.example.simpleweather.utils.allPermissionsGranted
import java.util.*

/**
 * Shared prefs:
 *  - locationName (this is always desired, if not found - current coordinates are used to fetch that info)
 */

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var navController: NavController
    private val sharedViewModel: LocationWeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(navController)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        sharedViewModel.locationName.observe(this) { newLocationName ->
            val savedLocationName = sharedPref.getString(Constants.SharedPreferences_locationName, null)

            if (savedLocationName == newLocationName || newLocationName == LocationWeatherViewModel.initialState.locationName.value) return@observe

            with (sharedPref.edit()) {
                putString(Constants.SharedPreferences_locationName, newLocationName)
                apply()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (permissions.all { Constants.RequiredPermissions_location.contains(it) }
            && grantResults.asList().allPermissionsGranted().not()
        ) {
            goToManualInput()
        }
    }

    private fun goToManualInput() {
        try {
            navController.navigate(R.id.action_startFragment_to_pickupFragment)
        } catch (e: IllegalArgumentException) {
            navController.navigate(R.id.action_flavorFragment_to_pickupFragment)
        }
    }
}

