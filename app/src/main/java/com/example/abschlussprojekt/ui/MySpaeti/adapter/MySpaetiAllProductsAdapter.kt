package com.example.abschlussprojekt.ui.MySpaeti.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.databinding.ListAllProductBinding
import com.example.abschlussprojekt.ui.ViewModel.MainViewModel


class MySpaetiAllProductsAdapter(
    private val dataset: List<Product>,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<MySpaetiAllProductsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ListAllProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListAllProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.binding.tvTitle.text = item.name
        holder.binding.tvPrice.text = item.price.toString() + " €"
        holder.binding.ivProduct.load(item.imageUrl).job.invokeOnCompletion {
                holder.binding.progressBar.visibility = View.GONE
            }
    }

}
