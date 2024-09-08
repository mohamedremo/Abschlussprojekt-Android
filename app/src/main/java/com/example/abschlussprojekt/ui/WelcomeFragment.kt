package com.example.abschlussprojekt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.databinding.FragmentWelcomeBinding
import com.example.abschlussprojekt.ui.viewmodel.FirebaseViewModel
import android.view.animation.AnimationUtils

private const val TAG = "WelcomeFragment"

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation Controller setzen.
        val navController = findNavController()

        //Wenn User bereits eingeloggt ist -> HomeFragment
        fireViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null)
                findNavController().navigate(
                    WelcomeFragmentDirections
                        .actionWelcomeFragmentToHomeFragment())
        }

        //Animation Test
        AnimationUtils.loadAnimation(requireContext(), R.anim.blink).also { anim ->
            binding.registerBtn.startAnimation(anim)
        }

        //LOGIN
        binding.loginBtn.setOnClickListener {
            navController.navigate(
                WelcomeFragmentDirections
                    .actionWelcomeFragmentToLogInFragment())
        }

        //REGISTER
        binding.registerBtn.setOnClickListener {
           navController.navigate(
               WelcomeFragmentDirections
                   .actionWelcomeFragmentToRegisterFragment())
        }
    }
}