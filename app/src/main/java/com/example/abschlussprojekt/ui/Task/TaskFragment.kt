package com.example.abschlussprojekt.ui.Task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.abschlussprojekt.databinding.FragmentTaskBinding
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel
import com.example.abschlussprojekt.ui.Task.viewmodel.TaskViewModel

class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private val viewModel: TaskViewModel by activityViewModels()
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fireViewModel.selectedTask.observe(viewLifecycleOwner) {
            binding.tvTaskTitle.text = it.taskName
            binding.tvTaskDescription.text = it.description
            binding.tvDueDate.text = it.expire
            binding.tvRewardPoints.text = it.butlePoints.toString() + " butlePoints"
        }

        binding.btnAccept.setOnClickListener {

        }
    }
}