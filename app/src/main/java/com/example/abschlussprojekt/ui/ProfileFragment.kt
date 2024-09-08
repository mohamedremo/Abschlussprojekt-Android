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

        binding.addBtn.setOnClickListener {
            fireViewModel.logOut()
        }

        //Wenn der User nicht eingeloggt ist wird er zum WelcomeFragment navigiert.
        fireViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null)
                findNavController().navigate(
                    ProfileFragmentDirections
                        .actionProfileFragmentToWelcomeFragment())
        }

        //Tasks werden beobachtet und die Recycler View wird aktualisiert.
        viewModel.tasks.observe(viewLifecycleOwner) {
            binding.recyV.adapter = TaskAdapter(viewModel.tasks.value!!.sortedBy {
                it.butlePoints
            }, viewModel, fireViewModel)
        }

        //Profil Referenz aus dem Firestore wird beobachtet und bei änderungen angezeigt.
        fireViewModel.profile.value?.addSnapshotListener { value, error ->
            if (error == null) {
                value?.let {
                    val firstName = it.getString("firstName")
                    val surName = it.getString("surName")
                    binding.tvName.setText("${firstName.toString()} ${surName.toString()}")
                }
            }
        }

    }
}