package com.example.abschlussprojekt.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.databinding.FragmentHomeBinding
import com.example.abschlussprojekt.setLottieByLevel
import com.example.abschlussprojekt.ui.viewmodel.FirebaseViewModel
import com.example.abschlussprojekt.ui.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    //ViewBinding, ActivityResultLauncher und ViewModel, die initialisiert werden.
    private lateinit var binding: FragmentHomeBinding
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private val viewModel: MainViewModel by activityViewModels()
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        //Initialisierung der ActivityResultLauncher
        initContent()

        fireViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null)
                nav.navigate(
                    HomeFragmentDirections
                        .actionHomeFragmentToWelcomeFragment())
        }

        fireViewModel.profile.value?.addSnapshotListener { value, error ->
            if (error == null) {

                val firstname = value?.get("firstName")
                    .toString()
                val points = value?.get("points")
                    .toString()

                //Lottie Animation für Level Anzeige
                val level = value?.get("level")
                    .toString()
                val lottieFile = setLottieByLevel(level.toInt())

                //Lottie Animation laden und starten
                binding.lvlSymbol.setAnimation(lottieFile)
                binding.lvlSymbol.playAnimation()

                binding.tvWelcome.text = "Welcome" + " " + firstname
                binding.tvPoints.text = points

                Log.d(TAG, "onViewCreated: Profile wurde geladen ${value}")

            } else {
                Log.d(TAG, "onViewCreated: Profile konnte nicht geladen werden")
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


        try {
            binding.ivProfilePic.setImageURI(fireViewModel.currentUser.value?.photoUrl)
            Log.d(TAG, "onViewCreated: Profil Pic set!")
        } catch (e: Exception) {
            Log.d(TAG, "onViewCreated: Cannot set Profil Pic!")
            e.printStackTrace()
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
