package com.example.abschlussprojekt.ui.MainMenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.abschlussprojekt.databinding.FragmentProfileBinding
import com.example.abschlussprojekt.setLottieByLevel
import com.example.abschlussprojekt.ui.Task.adapter.TaskAdapter
import com.example.abschlussprojekt.ui.Task.viewmodel.TaskViewModel

private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        binding.addBtn.setOnClickListener {
            nav.navigate(ProfileFragmentDirections.actionProfileFragmentToCreateTaskFragment())
        }

        //Profil Referenz aus dem Firestore wird beobachtet und bei änderungen angezeigt.
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            profile?.let {

                val firstName = it.firstName
                val surName = it.surName
                val lottieFile = setLottieByLevel(it.level)

                binding.tvName.setText("$firstName $surName")
                binding.tvRole.setText(it.role)
                binding.lvlSymbol.load(lottieFile) {
                    crossfade(true)
                    crossfade(500)
                }

                try {
                    binding.ivProfilePic.load(it.profilePicture) {
                        crossfade(true)
                        crossfade(500)
                    }
                    binding.ivAppBar.load(it.profilePicture) {
                        crossfade(true)
                        crossfade(500)
                    }
                    Log.d(TAG, "onViewCreated: Profil Pic set!")
                } catch (e: Exception) {
                    Log.d(TAG, "onViewCreated: Cannot set Profil Pic!")
                    e.printStackTrace()
                }
            }
        }
        viewModel.fetchTasks()
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            binding.recyV.adapter = TaskAdapter(tasks)
        }
    }

}
