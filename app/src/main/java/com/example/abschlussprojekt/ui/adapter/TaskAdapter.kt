package com.example.abschlussprojekt.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussprojekt.calculateDistance
import com.example.abschlussprojekt.databinding.ListTaskBinding
import com.example.abschlussprojekt.ui.viewmodel.MainViewModel
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.ui.viewmodel.FirebaseViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class TaskAdapter(
    private val dataset: List<Task>,
    private val viewModel: MainViewModel,
    private val firebaseViewModel: FirebaseViewModel
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

        //Längen und Breitengrad des aktuellen Users
        val myLatitude = 53.0793
        val myLongitude = 8.8017

        //Längen und Breitengrad des Tasks
        val itemLatitude = item.latitude
        val itemLongitude = item.longitude

        //Entfernung zwischen User und Task berechnen
        val distance = calculateDistance(itemLatitude, itemLongitude, myLatitude, myLongitude)

        //Distance auf eine Stelle nach dem Komma runden
        val distanceFormatted = String.format("%.1f", distance)

        //Text in TextView setzen
        holder.binding.tvTask.text = item.category
        holder.binding.tvDescription.text = item.description
        holder.binding.tvTitle.text = item.name
        holder.binding.tvPoints.text = item.butlePoints.toString()
        holder.binding.tvDistance.text = distanceFormatted
    }
}
