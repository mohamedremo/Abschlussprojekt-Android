package com.example.abschlussprojekt.ui.Task

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.data.model.Category
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.databinding.FragmentCreateTaskDetailBinding
import com.example.abschlussprojekt.outsideTouch
import com.example.abschlussprojekt.showDateTimePicker
import com.example.abschlussprojekt.ui.ViewModel.FirebaseViewModel
import com.google.android.gms.maps.MapView
import com.google.firebase.firestore.GeoPoint


class CreateTaskDetailFragment : Fragment() {

    private lateinit var binding: FragmentCreateTaskDetailBinding
    private val fireViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateTaskDetailBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainLayout = binding.mainLayout
        val mapView = binding.mapView.findViewById<View>(R.id.mapView)
        val nav = findNavController()

        binding.where.setOnClickListener {
            mapView.visibility = View.VISIBLE
            mainLayout.setOnTouchListener { _, event ->
                if (outsideTouch(mapView, event)) {
                    mapView.visibility = View.GONE
                    true
                } else {
                    false
                }
            }
        }

        binding.createTask.setOnClickListener {
            val what = binding.what.text.toString()
            val where = binding.where // GeoPoint aus Google muss noch gezogen werden.
            val wheen = binding.wheen.text.toString() // Date Time Picker
            val until = binding.until.text.toString() // Date Time Picker
            val points = binding.butlePoints.text.toString() // Zahlen Tastatur
            val description = binding.etDescription.text.toString()

            val newTask = Task(
                what,
                description,
                fireViewModel.currentUser.value?.uid.toString(),
                wheen,
                until,
                Category.DIENSTLEISTUNG,
                points.toInt(),
                GeoPoint(53.0793, 8.8017),
                false
            )
            clearFields()
            Log.d("CreateTaskDetailFragment", "New Task: $newTask")
            fireViewModel.saveTaskInFireStore(newTask)

        }

        binding.wheen.setOnClickListener {
            showDateTimePicker(requireContext()) { selectedDateTime ->
                binding.wheen.setText(selectedDateTime)
            }
        }

        binding.until.setOnClickListener {
            showDateTimePicker(requireContext()) { selectedDateTime ->
                binding.until.setText(selectedDateTime)
            }
        }

        binding.backBtn.setOnClickListener {
            nav.navigateUp()
        }
    }

    private fun clearFields() {
        binding.what.text?.clear()
        binding.where.text?.clear()
        binding.wheen.text?.clear()
        binding.until.text?.clear()
        binding.etDescription.text?.clear()
        binding.butlePoints.text?.clear()
    }
}
