package com.example.abschlussprojekt.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.abschlussprojekt.R
import com.example.abschlussprojekt.databinding.FragmentWelcomeBinding
import com.example.abschlussprojekt.ui.viewmodels.FirebaseViewModel
import android.view.animation.AnimationUtils

private const val TAG = "WelcomeFragment"

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    private val viewModel: FirebaseViewModel by activityViewModels()
    private lateinit var getContent: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        AnimationUtils.loadAnimation(requireContext(), R.anim.blink).also { anim ->
            binding.registerBtn.startAnimation(anim)
        }

//        viewModel.currentUser.observe(viewLifecycleOwner) { firebaseUser ->
//            if (firebaseUser != null) {
//                navController.navigate(R.id.action_welcomeFragment_to_homeFragment)
//            }
//        }

        binding.loginBtn.setOnClickListener {
            navController.navigate(R.id.action_welcomeFragment_to_logInFragment)
        }

        //FOTO UPLOAD FUNCTION
        binding.registerBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            getContent.launch(intent)
        }

        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    binding.ivProfilePic.setImageURI(uri)
                    viewModel.uploadImage(uri)
                }
            }
        }
    }

}