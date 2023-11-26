package com.example.cvicenie2.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.cvicenie2.R
import com.example.cvicenie2.data.DataRepository
import com.example.cvicenie2.databinding.FragmentProfilePhotoBinding
import com.example.cvicenie2.viewmodels.AuthViewModel
import java.io.File

class PhotoFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentProfilePhotoBinding
    private val SELECT_PICTURE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(DataRepository.getInstance(requireContext())) as T
                }
            }
        )[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
            BSelectImage.setOnClickListener {
                imageChooser()
            }
        }.also { bnd ->

            viewModel.photoResult.observe(viewLifecycleOwner) {
                Log.d("PhotoFragment", it.first)
                if (it.first == "success") {
                    requireView().findNavController().navigate(R.id.action_photo_profile)
                } else
                    Log.d("PhotoFragment", "failure")
            }

            viewModel.imageFile.observe(viewLifecycleOwner) { file ->
                // Update UI with the selected image file
                // For example, you might want to display the image in an ImageView
                if (file != null) {
                    binding.IVPreviewImage.setImageURI(file.toUri())
                }
            }
        }
    }

    private fun imageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            SELECT_PICTURE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    // Convert URI to File and set it in the ViewModel
                    val selectedImageFile = File(getRealPathFromURI(selectedImageUri))
                    viewModel.setImageFile(selectedImageFile)
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val contentResolver = requireActivity().contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val file = createTempFile("image", null, requireContext().cacheDir)

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }
}