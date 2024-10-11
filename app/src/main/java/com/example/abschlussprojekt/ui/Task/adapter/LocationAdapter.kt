package com.example.abschlussprojekt

import android.location.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abschlussprojekt.databinding.ListSearchLocationBinding

private const val TAG = "LocationAdapter"

class LocationAdapter(
    private val dataset: List<Address>,
    private val onLocationSelected: (Address) -> Unit,
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(val binding: ListSearchLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding =
            ListSearchLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = dataset[position]
        holder.binding.name.text = location.locality // Oder eine andere relevante Eigenschaft
        holder.binding.address.text =
            location.getAddressLine(0) // Zeigt die vollständige Adresse an


        // Setze einen Klick-Listener
        holder.itemView.setOnClickListener {
            onLocationSelected(location) // Rückgabe der ausgewählten Adresse
            notifyItemChanged(position) // Aktualisiert die Ansicht des geklickten Elements
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}