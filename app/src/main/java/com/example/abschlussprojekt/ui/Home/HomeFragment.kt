package com.example.abschlussprojekt.ui.Home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.abschlussprojekt.databinding.FragmentHomeBinding
import com.example.abschlussprojekt.displayTasksOnMap
import com.example.abschlussprojekt.setLottieByLevel
import com.example.abschlussprojekt.setOnlineStatus
import com.example.abschlussprojekt.stringToCelsius
import com.example.abschlussprojekt.ui.Home.viewmodel.HomeViewModel
import com.example.abschlussprojekt.ui.Task.viewmodel.TaskViewModel
import com.example.abschlussprojekt.welcomeMessage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    // ViewBinding, ActivityResultLauncher und ViewModel initialisieren
    private lateinit var binding: FragmentHomeBinding
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private val viewModel: HomeViewModel by activityViewModels()
    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private var myPosition = LatLng(53.0793, 8.8017)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // View Binding initialisieren
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // MapView aus dem Binding-Objekt holen
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        return binding.root
    }

    // Lebenszyklusmethoden für die MapView
    override fun onStart() {
        super.onStart()
        mapView.onStart()
        viewModel.fetchTasks() // Tasks laden
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    //-----------------------------------------------------------------------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        // Letzten Standort abrufen und aktualisieren

        binding.cardView2.setOnClickListener {
            nav.navigate(HomeFragmentDirections.actionHomeFragmentToLetsButleFragment())
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                myPosition = LatLng(it.latitude, it.longitude)
                viewModel.updateLastLocation(GeoPoint(it.latitude, it.longitude))
            }
        }

        // Initialisierung der ActivityResultLauncher
        initContent()


        // Profile Daten beobachten und bei Änderung TextView aktualisieren
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            Log.d(TAG, "onViewCreated: Profile wurde geladen $profile")

            if (profile != null) {
                // Profilbild laden
                binding.ivProfilePic.load(profile.profilePicture) {
                    crossfade(true)
                    crossfade(500)
                }

                val firstname = profile.firstName
                val points = profile.points.toString()

                // Lieferstatus Animation
                setOnlineStatus(binding.onlineStatus, profile.readyForWork)

                // Level Animation
                binding.lvlSymbol.setAnimation(setLottieByLevel(profile.level))
                binding.lvlSymbol.playAnimation()

                // Willkommenstext und Punkte aktualisieren
                binding.tvWelcome.text = welcomeMessage(firstname)
                binding.tvPoints.text = points
            }
        }

        // Tasks beobachten und Map initialisieren
        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            Log.d(TAG, "onViewCreated: Tasks wurden geladen $tasks")
            mapView.getMapAsync { googleMap ->
                // Überprüfen der Berechtigungen für den Standort
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Berechtigung anfordern, falls nicht erteilt
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1000
                    )
                    return@getMapAsync
                }

                // Standort aktivieren und Kamera bewegen
                googleMap.isMyLocationEnabled = true
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13f))
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                // Überprüfen, ob Tasks nicht leer sind
                if (tasks.isNotEmpty() && tasks != null) {
                    val boundsBuilder = LatLngBounds.Builder()
                    for (task in tasks) {
                        val position = LatLng(task.location.latitude, task.location.longitude)
                        googleMap.addMarker(
                            MarkerOptions().position(position)
                                .title(task.taskName)
                        )
                        boundsBuilder.include(position)
                    }
                    val bounds = boundsBuilder.build()


                    // Kamera auf die Bounds der Marker bewegen
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1))
                } else {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13f))
                    Log.d(TAG, "MapView: Keine Aufgaben gefunden")
                }

                Log.d(TAG, "onViewCreated: Tasks: $tasks")
                displayTasksOnMap(googleMap, tasks)

                // Marker-Klick-Listener
                googleMap.setOnMarkerClickListener { marker ->
                    val task = tasks?.find { task ->
                        task.taskName == marker.title.toString()
                    }
                    if (task != null) {
                        Log.d(TAG, "Task gefunden: $task")
                        taskViewModel.setSelectedTask(task)
                        nav.navigate(HomeFragmentDirections.actionHomeFragmentToTaskFragment())
                    } else {
                        Log.d(TAG, "Kein Task für den Marker gefunden")
                    }
                    true
                }
            }

            // Shop Button Klick-Listener
            binding.shopBtn.setOnClickListener {
                binding.shoppingBags.playAnimation()
                nav.navigate(HomeFragmentDirections.actionHomeFragmentToMySpaetiFragment())
            }

            // Wenn User Null ist, zum WelcomeFragment navigieren
            viewModel.currentUser.observe(viewLifecycleOwner) {
                if (it == null)
                    nav.navigate(
                        HomeFragmentDirections
                            .actionHomeFragmentToWelcomeFragment()
                    )
            }

            // Wetterdaten beobachten und bei Änderung TextView aktualisieren
            viewModel.lastWeather.observe(viewLifecycleOwner) {
                binding.tvWeather.text =
                    stringToCelsius(it.current_weather.temperature.toString())
            }

            // Klick-Listener für Profilbildänderung
            binding.cvProfile.setOnClickListener {
                pickImage()
                initContent()
                Log.d(TAG, "onViewCreated: Profilbild geändert")

            }

            // MySpaeti Button Klick-Listener
            binding.mySpaetiBtn.setOnClickListener {
                if (viewModel.profile.value?.readyForWork != true) {
                    viewModel.setOnlineStatus(true)
                } else
                    viewModel.setOnlineStatus(false)
            }

            // Floating Button Klick-Listener
            binding.floatingBtn.setOnClickListener {
                nav.navigate(HomeFragmentDirections.actionHomeFragmentToCreateTaskFragment())
            }
        }
    }


    //--------------------------------------------------------------------
    // Intent zum Foto Picken und dann mit uri hochladen
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        getContent.launch(intent)
    }

    // Initialisierung des ActivityResultLaunchers
    private fun initContent() {
        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    if (uri != null) {
                        Log.d(TAG, "initContent: $uri")
                        binding.ivProfilePic.setImageURI(uri)
                        viewModel.uploadImage(uri)
                    }
                }
            }
    }
}