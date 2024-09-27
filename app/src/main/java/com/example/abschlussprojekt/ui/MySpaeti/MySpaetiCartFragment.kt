package com.example.abschlussprojekt.ui.MySpaeti

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.databinding.FragmentMySpaetiCartBinding
import com.example.abschlussprojekt.ui.MySpaeti.adapter.MySpaetiAllProductsAdapter
import com.example.abschlussprojekt.ui.MySpaeti.adapter.MySpaetiCartAdapter
import com.example.abschlussprojekt.ui.ViewModel.MainViewModel

class MySpaetiCartFragment : Fragment() {

    private lateinit var binding: FragmentMySpaetiCartBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaetiCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = MySpaetiCartAdapter(viewModel.products, viewModel)
    }
}