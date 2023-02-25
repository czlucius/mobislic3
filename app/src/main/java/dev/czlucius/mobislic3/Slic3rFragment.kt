package dev.czlucius.mobislic3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.czlucius.mobislic3.databinding.FragmentSlic3Binding
import org.the3deer.android_3d_model_engine.collision.CollisionController
import org.the3deer.android_3d_model_engine.controller.TouchController
import org.the3deer.android_3d_model_engine.services.SceneLoader
import org.the3deer.android_3d_model_engine.view.ModelSurfaceView

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Slic3rFragment : Fragment() {

    private var _binding: FragmentSlic3Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val glView: ModelSurfaceView? = null
    private val touchController: TouchController? = null
    private val scene: SceneLoader? = null
    private val collisionController: CollisionController? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = FragmentSlic3Binding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}