package dev.czlucius.mobislic3

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.czlucius.mobislic3.databinding.FragmentSlic3Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.the3deer.android_3d_model_engine.camera.CameraController
import org.the3deer.android_3d_model_engine.collision.CollisionController
import org.the3deer.android_3d_model_engine.controller.TouchController
import org.the3deer.android_3d_model_engine.controller.TouchEvent
import org.the3deer.android_3d_model_engine.services.SceneLoader
import org.the3deer.android_3d_model_engine.view.ModelSurfaceView
import org.the3deer.util.event.EventListener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Slic3rFragment : Fragment() {

    private var _binding: FragmentSlic3Binding? = null
    private lateinit var contentResolver: ContentResolver

    private val args: Slic3rFragmentArgs by navArgs()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var glView: ModelSurfaceView? = null
    private var touchController: TouchController? = null
    private var scene: SceneLoader? = null
    private val collisionController: CollisionController? = null
    private var cameraController: CameraController? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        contentResolver = requireContext().applicationContext.contentResolver
        _binding = FragmentSlic3Binding.inflate(inflater, container, false)
//        binding.root.visibility = View.INVISIBLE
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var writeFileCallback: (OutputStream) -> Unit

    private val createFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data ?: return@registerForActivityResult
                contentResolver.openOutputStream(uri)?.let { it1 -> writeFileCallback(it1) }


            }
        }

    private fun createFile(pickerInitialUri: Uri? = null) {


        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "output.gcode")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        createFileLauncher.launch(intent)
    }

    fun Float.toDips() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics);
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fileUri = args.fileUri
        var inputStream: InputStream? = contentResolver.openInputStream(fileUri)!!
        val type = contentResolver.getType(fileUri)
        Log.i("dg2w5g", "type is $type")
        val fn = Date().toInstant().epochSecond.toString() + ".stl"
        val myFile = File(
            requireContext().filesDir,
            fn
        ) // filesDir - Your app's ordinary, persistent files reside directory
        val inputStream2: InputStream =contentResolver.openInputStream(fileUri)!!
        val o2s: OutputStream = FileOutputStream(myFile)
        val buf = inputStream2.readBytes()
        o2s.write(buf)
        o2s.close()
        inputStream2.close()



        Log.i("248fy83", myFile.toURI().toString())
        scene = SceneLoader(activity, myFile.toURI(), 1/*stl*/)
        cameraController = CameraController(scene!!.camera)

        touchController = TouchController(activity)
        touchController!!.addListener(EventListener {event ->
            val touchEvent = event as TouchEvent
            if (touchEvent.action == TouchEvent.Action.MOVE) {

                if (scene!!.selectedObject != null) {
                    scene!!.onEvent(event)
                } else {
                    cameraController!!.onEvent(event)
                    scene!!.onEvent(event)
                    if ((event as TouchEvent).action == TouchEvent.Action.PINCH) {
                        glView!!.onEvent(event)
                    }
                }
            }
            true
        })
        glView = ModelSurfaceView(activity, floatArrayOf(1f, 1f, 1f, 1f), scene)
        binding.viewer3d.addView(glView, ViewGroup.LayoutParams.MATCH_PARENT, 300f.toDips().toInt())
//        binding.viewer3d
        scene!!.init()
//        inputStream = buf.inputStream()


        CoroutineScope(Dispatchers.Main).launch {
            if (type != "application/vnd.ms-pki.stl") {
                goBackToUpload("File type is not STL", "Only STL files are supported")

            } else {
                Snackbar.make(view, "Slicing in progress...", Snackbar.LENGTH_SHORT).show()
                val resultByteArray = postISToURL(inputStream!!, "http://168.138.177.217:28508/3d")

                binding.root.visibility = View.VISIBLE

                writeFileCallback = {
                    it.write(resultByteArray)
                }
                binding.exportBtn.setOnClickListener {
                    createFile()
                }


            }
        }

//        try {
//            Log.i("ModelActivity", "Loading GLSurfaceView...")
//             glView = ModelSurfaceView(activity, floatArrayOf(0f, 0f, 0f), scene)
//            glView!!.addListener(this)
//        } catch (e: Exception) {
//            Log.e("ModelActivity", e.message, e)
//            Toast.makeText(
//                context, "Error loading OpenGL view:${e.message}", Toast.LENGTH_LONG).show()
//
//        }

    }

    @WorkerThread
    suspend fun postISToURL(inputStream: InputStream, url: String): ByteArray {

        return withContext(Dispatchers.IO) {
            try {
                val conn: HttpURLConnection =
                    URL(url).openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "model/stl")

                Log.i("dg2w5g", "set props")

                val wr = DataOutputStream(conn.outputStream)


                val buffer = ByteArray(4096)
                var read = 0

                while (inputStream.read(buffer).also { read = it } != -1) {
                    wr.write(buffer, 0, read)
                    Log.i("dg2w5g", "read: $read")

                }
                wr.flush()
                wr.close()


                val responseCode: Int = conn.responseCode
                Log.i("dg2w5g", responseCode.toString())

                val stream = conn.inputStream
                val bytes = stream.readBytes()
                try {
                    stream.close()
                } catch (e: IOException) {
                    // we don't want to throw a fatal error when we alr got bytes needed
                    return@withContext bytes
                }

                return@withContext bytes
            } catch (e: IOException) {
                e.printStackTrace()
                goBackToUpload("Error", "I/O error occurred")
                return@withContext byteArrayOf()
            }
        }

    }

    private suspend fun goBackToUpload(
        title: String,
        msg: String,
        positiveText: String = "Go back"
    ) {
        return suspendCoroutine { continuation ->
            requireActivity().runOnUiThread {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(positiveText) { _, _ ->
                        continuation.resume(Unit)

                        parentFragmentManager.popBackStack()
                    }
                    .setOnCancelListener { continuation.resume(Unit) }
                    .show()
            }
        }

    }
//    fun onEvent(event: EventObject): Boolean {
//        if (event is FPSEvent) {
//            gui.onEvent(event)
//        } else if (event is SelectedObjectEvent) {
//            gui.onEvent(event)
//        } else if (event.source is MotionEvent) {
//            // event coming from glview
//            touchController!!.onMotionEvent(event.source as MotionEvent)
//        } else if (event is CollisionEvent) {
//            scene!!.onEvent(event)
//        } else if (event is TouchEvent) {
//            if (event.action == TouchEvent.Action.CLICK) {
//                if (!collisionController!!.onEvent(event)) {
//                    scene!!.onEvent(event)
//                }
//            } else {
//                if (scene!!.selectedObject != null) {
//                    scene.onEvent(event)
//                } else {
//                    cameraController.onEvent(event)
//                    scene.onEvent(event)
//                    if (event.action == TouchEvent.Action.PINCH) {
//                        glView!!.onEvent(event)
//                    }
//                }
//            }
//        } else if (event is ViewEvent) {
//            val viewEvent = event
//            if (viewEvent.code == ViewEvent.Code.SURFACE_CHANGED) {
//                cameraController.onEvent(viewEvent)
//                touchController!!.onEvent(viewEvent)
//
//                // process event in GUI
//                if (gui != null) {
//                    gui.setSize(viewEvent.width, viewEvent.height)
//                    gui.setVisible(true)
//                }
//            } else if (viewEvent.code == ViewEvent.Code.PROJECTION_CHANGED) {
//                cameraController.onEvent(event)
//            }
//        }
//        return true
//    }


}