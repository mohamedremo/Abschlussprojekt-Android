package com.example.abschlussprojekt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.databinding.FragmentCreateTaskDetailBinding
import com.example.abschlussprojekt.ui.viewmodel.FirebaseViewModel


class CreateTaskDetailFragment : Fragment() {

    private lateinit var binding: FragmentCreateTaskDetailBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        ("Hier muss noch die Logik des erstellen eines neuen Tasks rein" +
//                "Dabei muss beachtet werden das wir das where in einen anderen Datentypen als wie unten beschrieben" +
//                "rein packen müssen" +
//                "" +
//                "Die Dates würde ich gerne mit einem DateTimePicker Klären um den Vorgang der UX zu verbessern")
//        val currentUser = fireViewModel.currentUser.value?.uid
//
//        binding.createTask.setOnClickListener {
//            val what = binding.what.text.toString()
//            val where = binding.where.text.toString()
//            val wheen = binding.wheen.text.toString()
//            val until = binding.until.text.toString()
//            val points = binding.butlePoints.text.toString()
//            val description = binding.etDescription.text.toString()
//
//            val newTask = Task(what,description,currentUser,wheen,until, selectedCategory,
//                listOf(), points.toInt(),where, false)
//        }

    }
}