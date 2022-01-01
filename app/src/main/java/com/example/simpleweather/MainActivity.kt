package com.example.simpleweather

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.simpleweather.models.AppViewModel
import com.example.simpleweather.utils.Constants
import com.example.simpleweather.utils.allPermissionsGranted
import java.util.*

/**
 * Shared prefs:
 *  - locationName (this is always desired, if not found - current coordinates are used to fetch that info)
 */

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var navController: NavController
    private val sharedViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(navController)

        sharedViewModel.location.observe(this) { newLocationName ->
            getPreferences(Context.MODE_PRIVATE).apply {
                val savedLocationName = AppViewModel.Location.parse(getString(Constants.SharedPreferences_location, null))

                if (newLocationName == savedLocationName) return@observe

                with (edit()) {
                    putString(Constants.SharedPreferences_location, newLocationName.toString())
                    apply()
                }
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

        if (permissions.all(Constants.RequiredPermissions_location::contains)
            && grantResults.asList().allPermissionsGranted().not()
        ) {
            goToManualInput()
        } else {
            restartFragment(R.id.nav_host_fragment)
        }
    }

    private fun restartFragment(fragmentId: Int) {
        val currentFragment = supportFragmentManager.findFragmentById(fragmentId)!!

        supportFragmentManager.beginTransaction()
            .detach(currentFragment)
            .commit()

        supportFragmentManager.beginTransaction()
            .attach(currentFragment)
            .commit()
    }

    private fun goToManualInput() {
        try {
            navController.navigate(R.id.action_startFragment_to_pickupFragment)
        } catch (e: IllegalArgumentException) {
            navController.navigate(R.id.action_flavorFragment_to_pickupFragment)
        }
    }
}

