package com.example.abschlussprojekt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.abschlussprojekt.R
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

        binding.registerBtn.animate()

        binding.registerBtn.setOnClickListener {
            val email = binding.etMail.text.toString()
            val password = binding.etPasswordOne.text.toString()
            val password2 = binding.etPasswordTwo.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val surName = binding.etSurName.text.toString()
            val birthDate = binding.etBirthDate.text.toString()
            val driverLicense = binding.radioButton.isChecked

            if (email.isEmpty() || password.isEmpty() || password2.isEmpty() ||
                firstName.isEmpty() || surName.isEmpty() || birthDate.isEmpty()) {
                toast("Bitte füllen Sie alle Felder aus")
                return@setOnClickListener
            }

            if (password != password2) {
                toast("Die eingegebenen Passwörter stimmen nicht überein, bitte versuchen sie es nochmal")
            } else if (password.length < 6) (
                toast("Das Passwort muss mindestens 6 Zeichen lang sein")
            ) else {
                clearFields()
                viewModel.registerNewUser(email, password)
                viewModel.saveInFirestore(
                    firstName, surName, email, birthDate, driverLicense, false, ""
                )
                toast("Erfolgreich registriert")
            }
        }

        binding.radioButton.setOnClickListener {
            if (binding.radioButton.isChecked) {
                binding.radioButton.isChecked = false
            } else {
                binding.radioButton.isChecked = true
            }
        }
    }

    private fun toast(text: String) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

    private fun clearFields() {
        binding.etFirstName.text?.clear()
        binding.etSurName.text?.clear()
        binding.etBirthDate.text?.clear()
        binding.etMail.text?.clear()
        binding.etPasswordOne.text?.clear()
        binding.etPasswordTwo.text?.clear()
    }
}

