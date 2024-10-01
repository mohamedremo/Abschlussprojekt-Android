package com.example.abschlussprojekt.ui.MySpaeti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.databinding.FragmentMySpaetiBinding
import com.example.abschlussprojekt.ui.MySpaeti.adapter.MySpaetiAllProductsAdapter
import com.example.abschlussprojekt.ui.MainMenu.viewmodel.MainViewModel
import com.example.abschlussprojekt.ui.MySpaeti.viewmodel.MySpaetiViewModel

class MySpaetiFragment : Fragment() {

    private lateinit var binding: FragmentMySpaetiBinding
    private val viewModel: MySpaetiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaetiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchProducts()

        viewModel.products.observe(viewLifecycleOwner) { products ->
            binding.recyclerView.adapter = MySpaetiAllProductsAdapter(products,viewModel)
        }

        binding.cartBtn.setOnClickListener {
            findNavController().navigate(MySpaetiFragmentDirections.actionMySpaetiFragmentToMySpaetiCartFragment())
        }
    }
}

