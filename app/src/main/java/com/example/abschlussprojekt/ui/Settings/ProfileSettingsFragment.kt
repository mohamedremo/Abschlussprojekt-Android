package com.example.abschlussprojekt.ui.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.abschlussprojekt.databinding.FragmentProfileSettingsBinding
import com.example.abschlussprojekt.setProfilePic
import com.example.abschlussprojekt.showDatePicker
import com.example.abschlussprojekt.showDateTimePicker
import com.example.abschlussprojekt.toast
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel

private const val TAG = "ProfileSettingsFragment"

class ProfileSettingsFragment : Fragment() {

    private lateinit var binding: FragmentProfileSettingsBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        fireViewModel.profile.observe(viewLifecycleOwner) { profile ->
            binding.ivProfilePic.load(profile?.profilePicture)
            binding.etsurName.setText(profile?.surName)
            binding.etFirstName.setText(profile?.firstName)
            binding.etBirthDate.setText(profile?.birthDate)
        }

        binding.btnSave.setOnClickListener {
            val surName = binding.etsurName.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val birthDate = binding.etBirthDate.text.toString()
            fireViewModel.updateProfile(firstName,surName, birthDate)
            toast("Profile erfolgreich aktualisiert", requireContext())
            nav.navigateUp()
        }

        binding.etBirthDate.setOnClickListener {
            showDatePicker(requireContext()) { selectedDateTime ->
                binding.etBirthDate.setText(selectedDateTime)
            }
        }

    }
}