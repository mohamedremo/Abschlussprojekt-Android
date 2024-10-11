package com.example.abschlussprojekt.ui.Task.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussprojekt.calculateDistance
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.databinding.ListTaskBinding
import com.example.abschlussprojekt.ui.Task.LetsButleFragmentDirections
import com.example.abschlussprojekt.ui.Task.viewmodel.TaskViewModel

private const val TAG = "TaskAdapter"

class TaskAdapter(
    private val dataset: List<Task>,
    private val viewModel: TaskViewModel
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ListTaskBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ListTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = dataset[position]

        //Text in TextView setzen
        holder.binding.tvTask.text = item.category
        holder.binding.tvDescription.text = item.description
        holder.binding.tvTitle.text = item.taskName
        holder.binding.tvPoints.text = item.butlePoints.toString()
        holder.binding.tvDeadline.text = item.expire

        holder.binding.root.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: ${item.location}")
            viewModel.setSelectedTask(item)
            holder.itemView.findNavController().navigate(
                LetsButleFragmentDirections.actionLetsButleFragmentToTaskFragment()
            )
        }
    }
}

