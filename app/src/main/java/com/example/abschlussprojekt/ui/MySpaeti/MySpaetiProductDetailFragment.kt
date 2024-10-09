package com.example.abschlussprojekt.ui.MySpaeti

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.abschlussprojekt.databinding.FragmentMySpaetiProductDetailBinding
import com.example.abschlussprojekt.ui.MySpaeti.viewmodel.MySpaetiViewModel

class MySpaetiProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentMySpaetiProductDetailBinding
    private val viewModel: MySpaetiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaetiProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nav = findNavController()


        viewModel.selectedProduct.observe(viewLifecycleOwner) {
            binding.tvPrice.text = it?.price.toString()
            binding.ivProduct.load(it?.imageUrl)
            binding.tvProductName.text = it.name
        }

        viewModel.productCount.observe(viewLifecycleOwner) {
            binding.tvCount.text = it.toString()

        }

        binding.lottiePlus.setOnClickListener {
            binding.lottiePlus.speed = 2f
            binding.lottiePlus.playAnimation()
            viewModel.increaseProductCount()
        }

        binding.lottieMinus.setOnClickListener {
            if (viewModel.productCount.value!! > 0) {
                binding.lottieMinus.speed = 2f
                binding.lottieMinus.playAnimation()
                viewModel.decreaseProductCount()
            }
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addProductToCart()
            nav.navigateUp()
        }

        binding.backBtn.setOnClickListener {
            nav.navigateUp()
        }

    }
}