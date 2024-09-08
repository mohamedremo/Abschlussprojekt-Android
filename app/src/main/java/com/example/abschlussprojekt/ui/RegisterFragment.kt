package com.example.abschlussprojekt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.databinding.FragmentRegisterBinding
import com.example.abschlussprojekt.toast
import com.example.abschlussprojekt.ui.viewmodel.FirebaseViewModel


private const val TAG = "RegisterFragment"

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Animation Testen
        binding.registerBtn.animate()

        //Wenn User bereits eingeloggt ist -> HomeFragment
        fireViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null)
                findNavController().navigate(
                    RegisterFragmentDirections
                        .actionRegisterFragmentToHomeFragment())
        }

        //Register Button
        binding.registerBtn.setOnClickListener {
            val email = binding.etMail.text.toString()
            val password = binding.etPasswordOne.text.toString()
            val password2 = binding.etPasswordTwo.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val surName = binding.etSurName.text.toString()
            val birthDate = binding.etBirthDate.text.toString()
            val driverLicense = binding.radioButton.isChecked

            //Wenn eines der benötigten Felder leer ist -> Toast!
            if (email.isEmpty() || password.isEmpty() || password2.isEmpty() ||
                firstName.isEmpty() || surName.isEmpty() || birthDate.isEmpty()
            ) {
                toast(getString(R.string.reg_allfields), context)
                return@setOnClickListener
            }

            if (password != password2) { // Wenn Passwörter nur nicht überein stimmen -> Toast!
                toast(getString(R.string.password_nomatch), context)
                return@setOnClickListener

            } else if (password.length < 6) { // Wenn Passwort weniger als 6 Zeichen -> Toast!
                toast(getString(R.string.password_sixchars), context)
                return@setOnClickListener

            }else {
                clearFields()
                fireViewModel.registerNewUser( //Registrierung wird mit eingegebenen Daten probiert.
                    email,
                    password,
                    firstName,
                    surName,
                    birthDate,
                    driverLicense,
                    readyForWork = false,
                    profilePicture = ""

                )
                toast(getString(R.string.reg_success), context) //Toast wenn Registrierung Erfolgreich
                findNavController().navigate(
                    RegisterFragmentDirections
                        .actionRegisterFragmentToHomeFragment())
            }
        }
    }


    //EditTexts leeren
    private fun clearFields() {
        binding.etFirstName.text?.clear()
        binding.etSurName.text?.clear()
        binding.etBirthDate.text?.clear()
        binding.etMail.text?.clear()
        binding.etPasswordOne.text?.clear()
        binding.etPasswordTwo.text?.clear()
    }

}
