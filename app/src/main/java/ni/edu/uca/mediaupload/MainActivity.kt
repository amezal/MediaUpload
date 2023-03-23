package ni.edu.uca.mediaupload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ni.edu.uca.mediaupload.databinding.ActivityMainBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var selectedImageUri: Uri? = null

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.200/filedb/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gridView = binding.gridView
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            openImageChooser();
        }

        GlobalScope.launch {
            try {
                val response = apiService.getAllImages()
                val url = "http://192.168.0.200/filedb/"
                val files = response.files.map {ImgFile("${url}${it.name}", it.isDir)}.toTypedArray()
                val adapter = ImageAdapter(files)
                gridView.adapter = adapter
            } catch (e: Exception) {
                Log.e("myapp", e.message!!)
            }
        }

    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    selectedImageUri?.let {
                        val file = File(getRealPathFromURI(it))
                        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
                        val image = MultipartBody.Part.createFormData("image", file.name, requestBody)
                        Log.e("myapp", file.totalSpace.toString())

                        GlobalScope.launch {
                            try {
                                apiService.uploadImage(file.name, requestBody)

                            } catch (e: Exception) {
                                Log.e("myapp", e.message!!)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor?.getString(it) }
        cursor?.close()
        return path ?: ""
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
    }
}