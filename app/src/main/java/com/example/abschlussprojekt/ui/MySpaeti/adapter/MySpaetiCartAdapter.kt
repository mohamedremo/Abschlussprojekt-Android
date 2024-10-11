package com.example.abschlussprojekt.ui.MySpaeti.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.databinding.ListCartProductsBinding
import com.example.abschlussprojekt.ui.MySpaeti.viewmodel.MySpaetiViewModel

private const val TAG = "MySpaetiCartAdapter"

class MySpaetiCartAdapter(
    private val dataset: MutableMap<Product, Int>,
    private val viewModel: MySpaetiViewModel
) : RecyclerView.Adapter<MySpaetiCartAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ListCartProductsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ListCartProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        //Da wir in diesem Adapter mit einer Map arbeiten wollen, müssen wir die Map in ihre Einzelteile zerlegen.
        val entry = dataset.entries.toList()[position]
        //entry.key ist das Produkt und entry.value ist die Anzahl
        val product = entry.key
        val count = entry.value

        val plusBtn = holder.binding.lottiePlus
        val minusBtn = holder.binding.lottieMinus

        //Setzen der Daten in die View
        holder.binding.tvCount.text = count.toString()
        holder.binding.tvTitle.text = product.name
        holder.binding.tvPrice.text = product.price.toString()
        holder.binding.ivProduct.load(product.imageUrl)
        holder.binding.tvDescription.text = product.type

        //Lottie Animationen für den Plus und Minus Button
        plusBtn.setOnClickListener {
            plusBtn.playAnimation()
            plusBtn.speed.times(1.5f)
            viewModel.increaseProductInCart(product)
        }

        minusBtn.setOnClickListener {
            minusBtn.playAnimation()
            viewModel.decreaseProductInCart(product)
        }

    }

}