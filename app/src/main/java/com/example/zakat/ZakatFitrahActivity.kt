package com.example.zakat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.zakat.databinding.ActivityMainBinding
import com.example.zakat.databinding.ActivityZakatFitrahBinding
import com.example.zakat.model.LoginResponse
import com.example.zakat.model.RegisterResponse
import com.example.zakat.retrofit.ApiConfig
import com.example.zakat.sharedpreferences.UserPreferences
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ZakatFitrahActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZakatFitrahBinding
    private var getFile: File? = null
    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Open the gallery and perform further operations
    private fun openGallery() {
        // Your code to open and read images from the gallery goes here
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityZakatFitrahBinding.inflate(layoutInflater)
        setContentView(binding.root);

        binding.btGambar.setOnClickListener { view->
            startGallery()
        }
        binding.buttonUpload.setOnClickListener { view ->
           upload()
        }

        val dropdown : AutoCompleteTextView = binding.autoComplete
        val items = listOf("Zakat Mal","Zakat Fitrah","Zakat Fidyah")
        val adapter = ArrayAdapter(this,R.layout.dropdown_item,items)

        dropdown.setAdapter(adapter)

        dropdown.onItemClickListener = AdapterView.OnItemClickListener{
            adapterView, view, i, l ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "items = $itemSelected", Toast.LENGTH_SHORT).show()
        }

    }


    private fun addZakat(file:File,tanggal:String,id_pembayar:String,pembayaran_uang:String,tanggungan:String){

        val TANGGAL = tanggal.toRequestBody("text/plain".toMediaType())
        val ID_PEMBAYAR = id_pembayar.toRequestBody("text/plain".toMediaType())
        val Pembayaran_uang = pembayaran_uang.toRequestBody("text/plain".toMediaType())
        val Tanggungan = tanggungan.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestImageFile
        )
        val client =ApiConfig.getApiService().addZakat(imageMultipart,TANGGAL,ID_PEMBAYAR,Pembayaran_uang,Tanggungan)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    Toast.makeText(this@ZakatFitrahActivity,responseBody.status,Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@ZakatFitrahActivity,"gagal",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                Toast.makeText(this@ZakatFitrahActivity,"ancur servernya",Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            binding.gambar.setImageURI(selectedImg)
            val myFile = uriToFile(selectedImg, this@ZakatFitrahActivity)
            getFile = myFile
        }
    }

    private fun upload(){

            val userPreferences = UserPreferences(this@ZakatFitrahActivity)
            addZakat(getFile as File,timeStamp,userPreferences.getId().toString(),binding.etjumlahUang.text.toString(),binding.etTanggungan.text.toString())

    }

    private fun getRealPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return ""
    }
}