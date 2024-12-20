package com.example.abschlussprojekt.ui.Task

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.databinding.FragmentLetsButleBinding
import com.example.abschlussprojekt.ui.Task.adapter.TaskAdapter
import com.example.abschlussprojekt.ui.Task.viewmodel.TaskViewModel

private const val TAG = "LetsButleFragment"

class LetsButleFragment : Fragment() {

    private lateinit var binding: FragmentLetsButleBinding
    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLetsButleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            binding.recyclerView.adapter = TaskAdapter(tasks, viewModel)
            Log.d(TAG, "tasks: $tasks")
            //binding.recyclerView.setHasFixedSize(true)
        }

        binding.toolbar.backBtn.setOnClickListener {
            nav.navigateUp()
        }
    }
}