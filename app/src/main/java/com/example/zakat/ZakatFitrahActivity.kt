package com.example.zakat

import android.Manifest
import android.app.AlertDialog
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
import com.google.android.material.snackbar.Snackbar
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
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val STORAGE_PERMISSION_REQUEST_CODE = 100
    }



    private fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun areStoragePermissionsGranted(): Boolean {
        val readPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
        return readPermissionCheck == PackageManager.PERMISSION_GRANTED
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
            if (i < 1){
                binding.etTanggungan.isEnabled =false
                binding.etTanggungan.setText("1")
                binding.etTanggungan.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
            }else{
                binding.etTanggungan.isEnabled =true
                binding.etTanggungan.setText("")
                binding.etTanggungan.setBackgroundColor(0x00000000)

            }
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
                    val intent = Intent(this@ZakatFitrahActivity, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ZakatFitrahActivity,"gagal",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                Toast.makeText(this@ZakatFitrahActivity,t.message,Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun startGallery() {
            if(!areStoragePermissionsGranted()){
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }else{
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, "Choose a Picture")
                launcherIntentGallery.launch(chooser)
            }



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

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startGallery()
                // Permission is granted. Continue the action or workflow in your
                // app.
            }else{
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Akses Media")
                dialogBuilder.setMessage("Untuk Melanjutkan Izinkan Aplikasi Mengakses Media")
                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    startGallery()
                }
                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                    // Handle the case where the user cancels the permission request or provide an alternative flow.
                }
                val dialog = dialogBuilder.create()
                dialog.show()
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