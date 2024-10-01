package com.example.abschlussprojekt.ui.MySpaeti.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.data.model.Category
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.databinding.ListAllProductBinding
import com.example.abschlussprojekt.ui.MainMenu.viewmodel.MainViewModel
import com.example.abschlussprojekt.ui.MySpaeti.MySpaetiFragmentDirections
import com.example.abschlussprojekt.ui.MySpaeti.viewmodel.MySpaetiViewModel


class MySpaetiAllProductsAdapter(
    private val dataset: List<Product>,
    private val viewModel: MySpaetiViewModel
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
        holder.binding.ivProduct.load(item.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.burger)
            error(android.R.drawable.stat_notify_error)
            listener (onSuccess = { _, _ ->
                holder.binding.progressBar.visibility = View.GONE})
        }

        holder.binding.root.setOnClickListener {
             viewModel.setSelectedProduct(item)
            holder.itemView.findNavController().navigate(MySpaetiFragmentDirections.actionMySpaetiFragmentToMySpaetiProductDetail())
        }


    }

}
