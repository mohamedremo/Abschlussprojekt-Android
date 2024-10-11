package com.example.abschlussprojekt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.abschlussprojekt.databinding.ActivityMainBinding
import com.example.abschlussprojekt.ui.Home.viewmodel.HomeViewModel
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.GeoPoint

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: HomeViewModel by viewModels()
    private val fireViewModel: FirebaseViewModel by viewModels()

    //Permission Launcher für den Zugriff auf die Standortberechtigungen
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            if (isGranted) {
                Log.i(TAG, "Berechtigung wurde erteilt: $permission")
            } else {
                Log.i(TAG, "Berechtigung wurde abgelehnt: $permission")
                showSettingsDialog()
                Toast.makeText(
                    this,
                    "Bitte erteile die vollständigen Berechtigungen um die App nutzen zu können",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        //Bottom Navigation
        val navHost =
            supportFragmentManager.findFragmentById(binding.navHostFragmentContainer.id) as NavHostFragment

        binding.bottomNav.setupWithNavController(navHost.navController)

        // Wird für Mockdaten verwendet.
        //initTasksFromJsonToFirebase(TAG, fireViewModel, assets)
        //initProfilesLocalToFirebase(TAG, fireViewModel, assets)


        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navHost.navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.profileFragment -> {
                    navHost.navController.navigate(R.id.profileFragment)
                    true
                }

                R.id.settingsFragment -> {
                    navHost.navController.navigate(R.id.settingsFragment)
                    true
                }

                else -> false
            }
        }

        //Bottom Navigation in bestimmten Fragmenten ausblenden.
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.logInFragment -> binding.bottomNav.visibility = View.GONE
                R.id.registerFragment -> binding.bottomNav.visibility = View.GONE
                R.id.welcomeFragment -> binding.bottomNav.visibility = View.GONE
                R.id.mySpaetiCartFragment -> binding.bottomNav.visibility = View.GONE
                R.id.mySpaetiProductDetailFragment -> binding.bottomNav.visibility = View.GONE
                else -> binding.bottomNav.visibility = View.VISIBLE
            }
        }


        //Zurück Button Funktion
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.navHostFragmentContainer.findNavController()
                    .navigateUp()
            }
        })

        //Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Permissions für die LocationServices checken.
        locationCheck()
    }

    private fun locationCheck() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    fireViewModel.profile.value?.apply { // longitude und latitude in database abspeichern
                        lastLocation = GeoPoint(latitude, longitude)
                    }
                    viewModel.getWeatherByLocation(latitude, longitude)
                } else {
                    requestNewLocationData()
                }
            }
        } else {
            requestInitialPermissions()
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        )
            .setMinUpdateIntervalMillis(2000)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        super.onLocationResult(locationResult)
                        val location = locationResult.lastLocation
                        if (location != null) {
                            val locationString =
                                "Latitude: " + location.latitude.toString() + "\nLongitude: " + location.longitude.toString()
                        } else {
                            // Standort immer noch nicht verfügbar, zeige dem Benutzer eine Fehlermeldung.
                            Toast.makeText(
                                this@MainActivity,
                                "Standort konnte nicht ermittelt werden!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }, null
            )
        }
    }

    private fun showRationaleDialog(onOk: () -> Unit) {
        MaterialAlertDialogBuilder(this).setTitle("Benötigt Berechtigung")
            .setMessage("Die App benötigt die folgende Berechtigung, um deine Aktivität zu tracken.")
            .setPositiveButton("OK") { _, _ -> onOk() }
            .show()
    }

    private fun requestInitialPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Zeige den Dialog zur Erklärung der Berechtigung
            showRationaleDialog {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {

            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Permission granted")
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this).setTitle("Berechtigungen dauerhaft abgelehnt")
            .setMessage(
                "Die Standortberechtigungen wurden dauerhaft abgelehnt." +
                        " Du musst sie in den App-Einstellungen manuell aktivieren."
            )
            .setPositiveButton("Zu den Einstellungen") { _, _ ->
                // Leite den Benutzer zu den App-Einstellungen
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }
}

