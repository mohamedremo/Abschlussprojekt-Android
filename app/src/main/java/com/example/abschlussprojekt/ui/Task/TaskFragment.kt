package com.example.abschlussprojekt.ui.Task

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.databinding.FragmentTaskBinding
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel
import com.example.abschlussprojekt.ui.Task.viewmodel.TaskViewModel

private const val TAG = "TaskFragment"

class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var getContent: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        viewModel.selectedTask.observe(viewLifecycleOwner) {
            binding.tvTaskTitle.text = it.taskName
            binding.tvTaskDescription.text = it.description
            binding.tvDueDate.text = it.expire
            binding.tvRewardPoints.text = it.butlePoints.toString() + " butlePoints"
            Log.d(TAG, "onViewCreated: $it")
        }

        binding.btnAccept.setOnClickListener {
            viewModel.selectedTask.value?.let { task ->
                val label = task.taskName + " von " + task.createdFrom
                val latitude = task.location.latitude
                val longitude = task.location.longitude
                val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent) // Google Maps öffnen
                }
            }
        }
    }
}