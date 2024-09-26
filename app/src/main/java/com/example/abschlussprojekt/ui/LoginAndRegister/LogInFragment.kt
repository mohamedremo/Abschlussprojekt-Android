package com.example.abschlussprojekt.ui.LoginAndRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.databinding.FragmentLogInBinding
import com.example.abschlussprojekt.ui.ViewModel.FirebaseViewModel

private const val TAG = "LogInFragment"

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        fireViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null)
                nav.navigate(
                    LogInFragmentDirections
                        .actionLogInFragmentToHomeFragment()
                )
        }

        //Button zum Einloggen
        binding.button.setOnClickListener {
            val email = binding.appCompatEditText.text.toString()
            val password = binding.appCompatEditText2.text.toString()
            fireViewModel.logIn(email, password)

            nav.navigate(
                LogInFragmentDirections
                    .actionLogInFragmentToHomeFragment()
            )
        }
    }

}