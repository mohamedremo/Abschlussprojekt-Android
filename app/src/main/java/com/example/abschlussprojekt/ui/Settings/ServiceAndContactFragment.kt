package com.example.abschlussprojekt.ui.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abschlussprojekt.databinding.FragmentServiceAndContactBinding

private const val TAG = "ServiceAndContactFragment"

class ServiceAndContactFragment : Fragment() {

    private lateinit var binding: FragmentServiceAndContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServiceAndContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}