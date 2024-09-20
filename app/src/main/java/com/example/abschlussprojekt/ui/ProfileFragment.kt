package com.example.abschlussprojekt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.databinding.FragmentProfileBinding
import com.example.abschlussprojekt.ui.adapter.TaskAdapter
import com.example.abschlussprojekt.ui.viewmodel.FirebaseViewModel
import com.example.abschlussprojekt.ui.viewmodel.MainViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.getTasks() AUSGESTELLT WEIL TASK MODEL BEARBEITET WIRD UND DIE ABHÄNGIKEITEN NOCH GESETZT WERDEN MIT MOCKAPI LEIDER NICHT MÖGLICH SOLVE? DATENSÄTZE SELBER AUF FIRESTORE BALLERN

        val nav = findNavController()

        binding.addBtn.setOnClickListener {
            fireViewModel.logOut()
        }

        //Wenn der User nicht eingeloggt ist wird er zum WelcomeFragment navigiert.
        fireViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null)
                nav.navigate(
                    ProfileFragmentDirections
                        .actionProfileFragmentToWelcomeFragment())
        }


        //Profil Referenz aus dem Firestore wird beobachtet und bei änderungen angezeigt.
        fireViewModel.profile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                 val firstName = it.firstName
                 val surName = it.surName
                 binding.tvName.setText("$firstName $surName")
                }
            }
        }

    }
