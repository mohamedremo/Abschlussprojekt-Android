package com.example.abschlussprojekt.ui.MainMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.abschlussprojekt.databinding.FragmentSettingsBinding
import com.example.abschlussprojekt.setLottieByLevel
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()


        binding.logout.setOnClickListener {
            fireViewModel.logOut()
            nav.navigate(SettingsFragmentDirections.actionSettingsFragmentToHomeFragment())
        }

        fireViewModel.profile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.lvlAnimation.setAnimation(setLottieByLevel(profile.level))
                binding.ivProfilePic.load(profile.profilePicture)
                binding.tvName.setText(profile.firstName + " " + profile.surName)
            }
        }

        binding.cvProfile.setOnClickListener {
            nav.navigate(SettingsFragmentDirections.actionSettingsFragmentToProfileSettingsFragment())
        }


    }

}