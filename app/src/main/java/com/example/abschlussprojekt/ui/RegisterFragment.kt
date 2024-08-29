package com.example.abschlussprojekt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.abschlussprojekt.databinding.FragmentRegisterBinding
import com.example.abschlussprojekt.ui.viewmodels.FirebaseViewModel

private const val TAG = "RegisterFragment"

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

        binding.registerBtn.setOnClickListener {
            val email = binding.etMail.text.toString()
            val password = binding.etPasswordOne.text.toString()
            val password2 = binding.etPasswordTwo.text.toString()

            if (email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                toast("Bitte füllen Sie alle Felder aus")
                return@setOnClickListener
            }

            if (password == password2) {
                viewModel.registerNewUser(email, password)
            } else {
                toast("Die eingegebenen Passwörter stimmen nicht überein, bitte versuch es nochmal")
            }
        }
    }
}