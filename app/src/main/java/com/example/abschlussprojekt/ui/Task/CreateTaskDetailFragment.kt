package com.example.abschlussprojekt.ui.Task

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abschlussprojekt.LocationAdapter
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.databinding.FragmentCreateTaskDetailBinding
import com.example.abschlussprojekt.outsideTouch
import com.example.abschlussprojekt.showDatePicker
import com.example.abschlussprojekt.toast
import com.example.abschlussprojekt.ui.Task.viewmodel.TaskViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private const val TAG = "CreateTaskDetailFragment"

class CreateTaskDetailFragment : Fragment() {

    private lateinit var binding: FragmentCreateTaskDetailBinding
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreateTaskDetailBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainLayout = binding.mainLayout
        val cvMapView = binding.cvMapView
        val nav = findNavController()

        getLastLocation() // Abrufen des Standorts

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            binding.tvNewTask.setText(category)
        }

        // Karte initialisieren
        mapView.getMapAsync { googleMap ->
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Berechtigung anfordern
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1000
                )
                return@getMapAsync
            }

            googleMap.isMyLocationEnabled = true // Aktivieren der Standortanzeige
        }

        // Suche nach Ort in der SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchLocation(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.where.setOnClickListener {
            cvMapView.visibility = View.VISIBLE
            mainLayout.setOnTouchListener { _, event ->
                if (outsideTouch(mapView, event)) {
                    cvMapView.visibility = View.GONE
                    true
                } else {
                    false
                }
            }
        }

        binding.createTask.setOnClickListener {
            if (binding.what.text.isNullOrEmpty() || binding.where.text.isNullOrEmpty()
                || binding.wheen.text.isNullOrEmpty() || binding.until.text.isNullOrEmpty()
                || binding.etDescription.text.isNullOrEmpty() || binding.butlePoints.text.isNullOrEmpty()
            ) {
                toast("Bitte füllen Sie alle Felder aus", requireContext())
                return@setOnClickListener
            } else if (binding.butlePoints.text.toString()
                    .toIntOrNull() == null || binding.butlePoints.text.toString()
                    .toInt() < 0
            ) {
                toast("Bitte geben Sie eine gültige Punktzahl ein", requireContext())
                return@setOnClickListener
            } else {
                val what = binding.what.text.toString()
                val where =
                    binding.where.text.toString() // GeoPoint aus Google muss noch gezogen werden.
                val wheen = binding.wheen.text.toString()
                val until = binding.until.text.toString()
                val points = binding.butlePoints.text.toString()
                val description = binding.etDescription.text.toString()

                val newTask = Task(
                    what,
                    description,
                    viewModel.currentUser.value?.uid.toString(),
                    wheen,
                    until,
                    viewModel.selectedCategory.value.toString(),
                    points.toInt(),
                    GeoPoint(
                        currentLocation?.latitude ?: 0.0,
                        currentLocation?.longitude ?: 0.0
                    ), // GeoPoint aktualisieren
                    false
                )
                clearFields()

                Log.d(TAG, "Neue Aufgabe: $newTask")
                viewModel.saveTaskInFireStore(newTask)

                toast("Task erstellt", requireContext())
                nav.navigate(CreateTaskDetailFragmentDirections.actionCreateTaskDetailFragmentToProfileFragment())
            }
        }

        binding.wheen.setOnClickListener {
            showDatePicker(requireContext()) { selectedDate ->
                binding.wheen.setText(selectedDate)
            }
        }

        binding.until.setOnClickListener {
            showDatePicker(requireContext()) { selectedDate ->
                binding.until.setText(selectedDate)
            }
        }

        binding.backBtn.setOnClickListener {
            nav.navigateUp()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
                viewModel.updateLastLocation(GeoPoint(location.latitude, location.longitude))
                Log.d(TAG, "Aktueller Standort: $currentLocation")
                // Karte zentrieren auf den aktuellen Standort
                mapView.getMapAsync { googleMap ->
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, 13f))
                }
            } else {
                Log.d(TAG, "Standort konnte nicht abgerufen werden.")
            }
        }
            .addOnFailureListener { e ->
                Log.e(TAG, "Fehler beim Abrufen des Standorts: ${e.message}")
            }
    }

    // Funktion zur Suche nach einem Ort
    private fun searchLocation(locationName: String) {
        if (Geocoder.isPresent()) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val addresses = withContext(Dispatchers.IO) {
                        geocoder.getFromLocationName(locationName, 4) // Nur ein Ergebnis abrufen
                    }
                    if (addresses != null && addresses.isNotEmpty()) {
                        displayLocations(addresses)
                        val address = addresses[0]
                        currentLocation = LatLng(address.latitude, address.longitude)
                        binding.where.setText(locationName)

                        // Karte zentrieren auf den gefundenen Standort
                        mapView.getMapAsync { googleMap ->
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    currentLocation!!,
                                    10f
                                )
                            )
                        }

                        Log.d(
                            TAG,
                            "Ort gefunden: $locationName, Koordinaten: ${address.latitude}, ${address.longitude}"
                        )
                    } else {
                        Log.d(TAG, "Ort nicht gefunden.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Fehler bei der Standortsuche: ${e.message}")
                }
            }
        } else {
            Log.e(TAG, "Geocoder nicht verfügbar.")
        }
    }

    private fun clearFields() {
        binding.what.text?.clear()
        binding.where.text?.clear()
        binding.wheen.text?.clear()
        binding.until.text?.clear()
        binding.etDescription.text?.clear()
        binding.butlePoints.text?.clear()
    }

    private fun displayLocations(locations: List<Address>) {
        val adapter = LocationAdapter(locations) { selectedLocation ->

            binding.where.setText(selectedLocation.getAddressLine(0))

            binding.cvMapView.visibility = View.INVISIBLE

            // Optional: Karte zentrieren auf den ausgewählten Standort
            mapView.getMapAsync { googleMap ->
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            selectedLocation.latitude,
                            selectedLocation.longitude
                        ), 13f
                    )
                )
            }
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}