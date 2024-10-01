package com.example.abschlussprojekt.ui.MySpaeti

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.abschlussprojekt.databinding.FragmentMySpaetiCartBinding
import com.example.abschlussprojekt.toast
import com.example.abschlussprojekt.ui.MySpaeti.adapter.MySpaetiCartAdapter
import com.example.abschlussprojekt.ui.MainMenu.viewmodel.MainViewModel

class MySpaetiCartFragment : Fragment() {

    private lateinit var binding: FragmentMySpaetiCartBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySpaetiCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = MySpaetiCartAdapter(viewModel.products, viewModel)

        binding.btnCheckOut.setOnClickListener {
            binding.cvSuccess.visibility = View.VISIBLE
            binding.cvSuccess.animate()
                .alpha(1f)
                .setDuration(2500)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                        toast("Check Out erfolgreich!", requireContext())
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        binding.cvSuccess.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {

                    }
                })
        }
    }
}