package com.example.abschlussprojekt.ui.MySpaeti

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.databinding.FragmentMySpaetiBinding
import com.example.abschlussprojekt.ui.MySpaeti.adapter.MySpaetiAllProductsAdapter
import com.example.abschlussprojekt.ui.ViewModel.MainViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MySpaetiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MySpaetiFragment : Fragment() {

    private lateinit var binding: FragmentMySpaetiBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaetiBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = MySpaetiAllProductsAdapter(products,viewModel)
    }
}

val products = listOf(
    Product(
        name = "Chips",
        price = 2.49,
        imageUrl = "https://example.com/chips.jpg",
        isAlcoholic = false,
        isVegan = true,
        isVegetarian = true,
        tags = listOf("bio", "glutenfree")
    ),
    Product(
        name = "Schokolade",
        price = 1.99,
        imageUrl = "https://example.com/schokolade.jpg",
        isAlcoholic = false,
        isVegan = false,
        isVegetarian = true,
        tags = listOf("halal", "sugarfree")
    ),
    Product(
        name = "Energydrink",
        price = 1.79,
        imageUrl = "https://example.com/energydrink.jpg",
        isAlcoholic = false,
        isVegan = true,
        isVegetarian = true,
        tags = listOf("sugarfree")
    ),
    Product(
        name = "Instantnudeln",
        price = 0.99,
        imageUrl = "https://example.com/instantnudeln.jpg",
        isAlcoholic = false,
        isVegan = true,
        isVegetarian = true,
        tags = listOf("bio", "glutenfree")
    ),
    Product(
        name = "Fruchtsaft",
        price = 2.29,
        imageUrl = "https://example.com/fruchtsaft.jpg",
        isAlcoholic = false,
        isVegan = true,
        isVegetarian = true,
        tags = listOf("bio", "sugarfree")
    ),
    Product(
        name = "Kekse",
        price = 1.49,
        imageUrl = "https://example.com/kekse.jpg",
        isAlcoholic = false,
        isVegan = false,
        isVegetarian = true,
        tags = listOf("halal")
    ),
    Product(
        name = "Nüsse",
        price = 3.99,
        imageUrl = "https://example.com/nuesse.jpg",
        isAlcoholic = false,
        isVegan = true,
        isVegetarian = true,
        tags = listOf("bio", "glutenfree")
    ),
    Product(
        name = "Gummibärchen",
        price = 1.59,
        imageUrl = "https://example.com/gummibaerchen.jpg",
        isAlcoholic = false,
        isVegan = false,
        isVegetarian = true,
        tags = listOf("sugarfree")
    ),
    Product(
        name = "Popcorn",
        price = 2.99,
        imageUrl = "https://example.com/popcorn.jpg",
        isAlcoholic = false,
        isVegan = true,
        isVegetarian = true,
        tags = listOf("bio")
    ),
    Product(
        name = "Pudding",
        price = 1.89,
        imageUrl = "https://example.com/pudding.jpg",
        isAlcoholic = false,
        isVegan = false,
        isVegetarian = true,
        tags = listOf("halal")
    )
)