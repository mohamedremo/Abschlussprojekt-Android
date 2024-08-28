package com.example.abschlussprojekt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.abschlussprojekt.databinding.FragmentLogInBinding
import androidx.fragment.app.viewModels
import com.example.abschlussprojekt.ui.viewmodels.FirebaseViewModel

private const val TAG = "LogInFragment"

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val viewModel: FirebaseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val email = binding.appCompatEditText.text.toString()
            val password = binding.appCompatEditText2.text.toString()
            viewModel.logIn(email, password)
        }
    }

}