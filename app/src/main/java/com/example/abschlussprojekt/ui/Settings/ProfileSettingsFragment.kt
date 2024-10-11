package com.example.abschlussprojekt.ui.Settings

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
import coil.load
import com.example.abschlussprojekt.databinding.FragmentProfileSettingsBinding
import com.example.abschlussprojekt.pickImage

import com.example.abschlussprojekt.showDatePicker
import com.example.abschlussprojekt.showDateTimePicker
import com.example.abschlussprojekt.toast
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel
import com.example.abschlussprojekt.ui.Settings.viewmodel.SettingsViewModel

private const val TAG = "ProfileSettingsFragment"

class ProfileSettingsFragment : Fragment() {

    private lateinit var binding: FragmentProfileSettingsBinding
    private val viewModel: SettingsViewModel by activityViewModels()
    private lateinit var getContent: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initContent()

        val nav = findNavController()

        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            Log.d(TAG, "Profile: $profile")
            binding.ivProfilePic.load(profile?.profilePicture)
            binding.etsurName.setText(profile?.surName)
            binding.etFirstName.setText(profile?.firstName)
            binding.etBirthDate.setText(profile?.birthDate)
        }

        binding.btnSave.setOnClickListener {
            val surName = binding.etsurName.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val birthDate = binding.etBirthDate.text.toString()
            viewModel.updateProfile(firstName, surName, birthDate)
            Log.d(TAG, "Profile erfolgreich aktualisiert $surName $firstName $birthDate")
            toast("Profile erfolgreich aktualisiert", requireContext())
            nav.navigateUp()
        }

        binding.ivProfilePic.setOnClickListener {
            pickImage(getContent)
            Log.d(TAG, "Profile Pic geändert")
        }

        binding.etBirthDate.setOnClickListener {
            showDatePicker(requireContext()) { selectedDateTime ->
                binding.etBirthDate.setText(selectedDateTime)
            }
        }

        binding.toolbar.backBtn.setOnClickListener {
            nav.navigateUp()
        }
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