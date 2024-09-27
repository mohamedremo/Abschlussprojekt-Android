package com.example.abschlussprojekt.ui.MySpaeti.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.databinding.ListCartProductsBinding
import com.example.abschlussprojekt.ui.ViewModel.MainViewModel
import com.example.abschlussprojekt.vibratePhone

class MySpaetiCartAdapter(
    private val dataset: List<Product>,
    private val viewModel: MainViewModel
): RecyclerView.Adapter<MySpaetiCartAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ListCartProductsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListCartProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        val plusBtn = holder.binding.lottiePlus
        val minusBtn = holder.binding.lottieMinus

        plusBtn.setOnClickListener {
            plusBtn.playAnimation()
            plusBtn.speed.times(1.5f)
            //viewModel.increaseProductCount(item)
        }

        minusBtn.setOnClickListener {
            minusBtn.playAnimation()
            //viewModel.decreaseProductCount(item)
        }

    }

}