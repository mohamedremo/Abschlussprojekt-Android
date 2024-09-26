package com.example.abschlussprojekt.ui.Task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.databinding.FragmentCreateTaskBinding
import com.example.abschlussprojekt.ui.ViewModel.MainViewModel


class CreateTaskFragment : Fragment() {

    private lateinit var binding: FragmentCreateTaskBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        binding.etwasAbholen.setOnClickListener {
            nav.navigate(R.id.action_createTaskFragment_to_createTaskDetailFragment)
        }
        binding.rent.setOnClickListener {
            nav.navigate(R.id.action_createTaskFragment_to_createTaskDetailFragment)
        }
        binding.wish.setOnClickListener {
            nav.navigate(R.id.action_createTaskFragment_to_createTaskDetailFragment)
        }
        binding.handyman.setOnClickListener {
            nav.navigate(R.id.action_createTaskFragment_to_createTaskDetailFragment)
        }

        binding.backBtn.setOnClickListener {
            nav.navigateUp()
        }
    }
}