package dev.czlucius.mobislic3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dev.czlucius.mobislic3.databinding.FragmentUploadBinding


class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    val binding: FragmentUploadBinding
        get() = _binding!!

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Uri? = result.data?.data
            if (data == null) {
                view?.let { Snackbar.make(it, "Invalid file", Snackbar.LENGTH_SHORT).show() }
                return@registerForActivityResult
            }
            // Since a Uri is a Parcelable, we can pass this to the Slicer fragment via SafeArgs.
            val directions = UploadFragmentDirections.passFile(data)

            findNavController().navigate(directions)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return _binding!!.root
    }


    fun openFile(pickerInitialUri: Uri?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }


        resultLauncher.launch(intent)

    }







    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.uploadButton.setOnClickListener {
            // Upload the files here via SAF.
            openFile(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}