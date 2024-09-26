package com.example.abschlussprojekt.ui.MainMenu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.databinding.FragmentHomeBinding
import com.example.abschlussprojekt.setLottieByLevel
import com.example.abschlussprojekt.ui.ViewModel.FirebaseViewModel
import com.example.abschlussprojekt.ui.ViewModel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    //ViewBinding, ActivityResultLauncher und ViewModel, die initialisiert werden.
    private lateinit var binding: FragmentHomeBinding
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private val viewModel: MainViewModel by activityViewModels()
    private val fireViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var mapView: MapView

    override fun onStart() {
        super.onStart()
        mapView.onStart()
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

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPosition = com.google.android.gms.maps.model.LatLng(53.0793, 8.8017)

        val nav = findNavController()

        binding.shopBtn.setOnClickListener {
            binding.shoppingBags.playAnimation()
            nav.navigate(HomeFragmentDirections.actionHomeFragmentToMySpaetiFragment())
        }

        mapView.getMapAsync {
            it.isMyLocationEnabled = true
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13f))
        }

        //Initialisierung der ActivityResultLauncher
        initContent()

        fireViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null)
                nav.navigate(
                    HomeFragmentDirections
                        .actionHomeFragmentToWelcomeFragment()
                )
        }


        fireViewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                val firstname = profile?.firstName
                    .toString()
                val points = profile?.points
                    .toString()

                //Lottie Animation für Level Anzeige
                val level = profile?.level
                    .toString()
                val lottieFile = setLottieByLevel(level.toInt())

                //Lottie Animation laden und starten
                binding.lvlSymbol.setAnimation(lottieFile)
                binding.lvlSymbol.playAnimation()

                binding.tvWelcome.text = "Welcome $firstname"
                binding.tvPoints.text = points

                try {
                    binding.ivProfilePic.setImageURI(fireViewModel.currentUser.value?.photoUrl)
                    Log.d(TAG, "onViewCreated: Profil Pic set!")
                } catch (e: Exception) {
                    Log.d(TAG, "onViewCreated: Cannot set Profil Pic!")
                    e.printStackTrace()
                }

                Log.d(TAG, "onViewCreated: Profile wurde geladen ${profile}")

            }
        }

        //Wetter daten observieren und bei Änderung TextView aktualisieren
        viewModel.lastWeather.observe(viewLifecycleOwner) {
            binding.tvWeather.text = it.current_weather.temperature.toString() + "°C"
        }

        //ImageView für Profilbild änderung klickbar gemacht.
        binding.cvProfile.setOnClickListener {
            uploadImage()
        }

        binding.floatingBtn.setOnClickListener {
            nav.navigate(HomeFragmentDirections.actionHomeFragmentToCreateTaskFragment())
        }

    }

    //--------------------------------------------------------------------
    //Intent zum Foto Picken und dann mit uri hochladen.
    private fun uploadImage() {
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
