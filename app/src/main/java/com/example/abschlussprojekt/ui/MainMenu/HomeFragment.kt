package com.example.abschlussprojekt.ui.MainMenu
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
import com.example.abschlussprojekt.setProfilePic
import com.example.abschlussprojekt.stringToCelsius
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel
import com.example.abschlussprojekt.ui.MainMenu.viewmodel.WeatherViewModel
import com.example.abschlussprojekt.welcomeMessage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    //ViewBinding, ActivityResultLauncher und ViewModel, die initialisiert werden.
    private lateinit var binding: FragmentHomeBinding
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private val viewModel: WeatherViewModel by activityViewModels()
    private val fireViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // View Binding initialisieren
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // MapView aus dem Binding-Objekt holen
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        fireViewModel.fetchTasks()
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialisierung der ActivityResultLauncher
        initContent()

        val myPosition = LatLng(53.089942, 8.829095)

        val nav = findNavController()

        binding.shopBtn.setOnClickListener {
            binding.shoppingBags.playAnimation()
            nav.navigate(HomeFragmentDirections.actionHomeFragmentToMySpaetiFragment())
        }

        /*Hier werden dies Tasks Observiert dementsprechend wird die Map initialisiert.
        * danach wird die ui angepasst und am ende werden die offenen Tasks auf der Karte angezeigt.*/

        fireViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
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
                if (tasks.isNotEmpty()) {
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

                    // Kamera auf die Bounds der Marker bewegen, animateCamera statt moveCamera
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
                } else {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13f))
                    Log.d(TAG, "MapView: Keine Aufgaben gefunden")
                }

                Log.d(TAG, "onViewCreated: Tasks: $tasks")
                displayTasksOnMap(googleMap, tasks)

                googleMap.setOnMarkerClickListener { marker ->
                    val task = tasks.find { it.taskName == marker.title.toString() }
                    if (task != null) {
                        Log.d(TAG, "onViewCreated: Task gefunden: $task")
                        fireViewModel.setSelectedTask(task)
                        nav.navigate(HomeFragmentDirections.actionHomeFragmentToTaskFragment())
                    }
                    true
                }
            }


            //Wenn User Null ist wird er zum WelcomeFragment navigiert.
            fireViewModel.currentUser.observe(viewLifecycleOwner) {
                if (it == null)
                    nav.navigate(
                        HomeFragmentDirections
                            .actionHomeFragmentToWelcomeFragment()
                    )
            }

            //Profile Daten observieren und bei Änderung TextView aktualisieren
            fireViewModel.profile.observe(viewLifecycleOwner) { profile ->

                Log.d(TAG, "onViewCreated: Profile wurde geladen $profile")

                if (profile != null) {

                    val firstname = profile.firstName
                    val points = profile.points.toString()


                    binding.ivProfilePic.load(profile.profilePicture)

                    //---------------LIEFERSTATUS ANIMATION--------------------------
                    setOnlineStatus(binding.onlineStatus, profile.readyForWork)

                    //---------------LEVEL ANIMATION--------------------------
                    binding.lvlSymbol.setAnimation(setLottieByLevel(profile.level))
                    binding.lvlSymbol.playAnimation()

                    binding.tvWelcome.text = welcomeMessage(firstname)
                    binding.tvPoints.text = points

                }
            }

            //Wetter daten observieren und bei Änderung TextView aktualisieren
            viewModel.lastWeather.observe(viewLifecycleOwner) {
                binding.tvWeather.text = stringToCelsius(it.current_weather.temperature.toString())
            }

            //ImageView für Profilbild änderung klickbar gemacht.
            binding.cvProfile.setOnClickListener {
                pickImage()
            }

            binding.mySpaetiBtn.setOnClickListener {
                if (fireViewModel.profile.value?.readyForWork != true) {
                    fireViewModel.setOnlineStatus(true)
                } else
                    fireViewModel.setOnlineStatus(false)
            }

            binding.floatingBtn.setOnClickListener {
                nav.navigate(HomeFragmentDirections.actionHomeFragmentToCreateTaskFragment())
            }

        }
    }

        //--------------------------------------------------------------------
        //Intent zum Foto Picken und dann mit uri hochladen.
        private fun pickImage() {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            getContent.launch(intent)
        }

        private fun initContent() {
            getContent =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val uri: Uri? = result.data?.data
                        if (uri != null) {
                            binding.ivProfilePic.setImageURI(uri)
                            fireViewModel.uploadImage(uri)
                        }
                    }
                }
        }
    }


