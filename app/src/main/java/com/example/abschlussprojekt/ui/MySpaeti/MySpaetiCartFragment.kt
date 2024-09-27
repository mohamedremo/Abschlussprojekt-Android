package com.example.abschlussprojekt.ui.MySpaeti

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.databinding.FragmentMySpaetiCartBinding

class MySpaetiCartFragment : Fragment() {

    private lateinit var binding: FragmentMySpaetiCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaetiCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}