package com.example.abschlussprojekt.ui.MySpaeti.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.databinding.ListAllProductBinding
import com.example.abschlussprojekt.formatToEuro
import com.example.abschlussprojekt.toast
import com.example.abschlussprojekt.ui.MySpaeti.MySpaetiFragmentDirections
import com.example.abschlussprojekt.ui.MySpaeti.viewmodel.MySpaetiViewModel

private const val TAG = "MySpaetiAllProductsAdapter"

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

        //Hier wird die Hintergrund Farbe des Jeweiligen Produktes festgelegt.
        if (item.type == "snack") {
            holder.binding.imageBackground.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_tertiary))
        } else if (item.type == "drink") {
            holder.binding.imageBackground.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_tertiaryFixed))
        } else if (item.type == "sonstiges") {
            holder.binding.imageBackground.setBackgroundColor(holder.itemView.context.getColor(R.color.md_theme_secondary))
        }

        //Hier wird die Funktion des Like Buttons beim Klicken ausgelöst
        holder.binding.likeBtn.setOnClickListener {
            viewModel.toggleFavorite(item)
            //Hier wird dem Adapter mitgeteilt dass das aktuelle Item geändert wurde.
            notifyItemChanged(position)
        }

        //Hier wird der Status von den Favoriten abgefragt und dem Button entsprechend gesetzt.
        if (viewModel.favoriteProducts.value?.contains(item) == true) {
            holder.binding.likeBtn.setImageResource(R.drawable.heart_focus)
        } else {
            holder.binding.likeBtn.setImageResource(R.drawable.heart_unfocus)
        }

        //Hier werden die jewiligen UI-Elemente mit den Daten des Produktes gefüllt.
        holder.binding.tvTitle.text = item.name
        holder.binding.tvPrice.text = formatToEuro(item.price)

        //Hier wird das Produktbild mit Hilfe der Coil-Bibliothek geladen.
        holder.binding.ivProduct.load(item.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.burger)
            error(android.R.drawable.stat_notify_error)
            //Ein Listener wird gesetzt, wenn Erfolgreich --> Progressbar wird ausgeblendet.
            listener (onSuccess = { _, _ ->
                holder.binding.progressBar.visibility = View.GONE})
        }

        holder.binding.arrowBtn.setOnClickListener {
            viewModel.simpleAddToCart(item)
            toast("${item.name} wurde zum Warenkorb hinzugefügt.", holder.itemView.context)

        }

        //Hier wird die LiveData von "selectedProduct" beim Klick auf das jeweilige Produkt gesetzt und navigiert.
        holder.binding.root.setOnClickListener {
             viewModel.setSelectedProduct(item)
            holder.itemView.findNavController().navigate(MySpaetiFragmentDirections.actionMySpaetiFragmentToMySpaetiProductDetail())
        }


    }

}
