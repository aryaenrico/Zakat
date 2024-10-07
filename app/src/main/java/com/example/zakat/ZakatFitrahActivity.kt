package com.example.zakat

import android.Manifest

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zakat.databinding.ActivityZakatFitrahBinding
import com.example.zakat.model.RegisterResponse
import com.example.zakat.retrofit.ApiConfig
import com.example.zakat.sharedpreferences.UserPreferences
import com.example.zakat.viewModel.ZakatViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ZakatFitrahActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZakatFitrahBinding
    private lateinit var viewModel: ZakatViewModel
    private var flagJenisZakat = 0;
    private var flagJenisSumbangan = 0;
    private lateinit var userPreferences: UserPreferences
    private var getFile: File? = null
    private var check: Boolean = false
    private val REQUEST_READ_STORAGE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZakatFitrahBinding.inflate(layoutInflater)
        setContentView(binding.root);
        viewModel = ViewModelProvider(this).get(ZakatViewModel::class.java)
        userPreferences = UserPreferences(this@ZakatFitrahActivity)

        binding.btGambar.setOnClickListener { view ->
            startGallery()
        }

        viewModel.loading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }

        }


        val dropdown: AutoCompleteTextView = binding.autoComplete
        val items = listOf("Zakat Mal", "Zakat Fidyah", "Zakat Fitrah")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, items)

        val dropdownSumbangan: AutoCompleteTextView = binding.autoComplete2
        val itemsSumbangan = listOf("infak", "sedekah")
        val adapterSumbangan = ArrayAdapter(this, R.layout.dropdown_item, itemsSumbangan)

        binding.checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.setTambahan(true);
            } else {
                viewModel.setTambahan(false);
            }
            check = isChecked
        })

        viewModel.tambahan.observe(this) {
            if (!it) {
                binding.container.visibility = View.GONE

            } else {
                binding.container.visibility = View.VISIBLE
            }
        }

        dropdown.setAdapter(adapter)
        dropdownSumbangan.setAdapter(adapterSumbangan)
        dropdown.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            flagJenisZakat = i + 1;
            if (i < 2) {
                binding.etTanggungan.isEnabled = false
                binding.etTanggungan.setText("1")
                binding.etTanggungan.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
            } else {
                binding.etTanggungan.isEnabled = true
                binding.etTanggungan.setText("")
                binding.etTanggungan.setBackgroundColor(0x00000000)

            }
        }

        dropdownSumbangan.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                flagJenisSumbangan = i + 4;

            }
        binding.buttonUpload.setOnClickListener { view ->

            if (!checkField()) {
                if (check == true) {
                    if (!checkFieldSumbangan()) {
                        binding.buttonUpload.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonUpload.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.red
                            )
                        )
                        viewModel.sendZakat(
                            getFile as File,
                            timeStamp,
                            userPreferences.getId().toString(),
                            binding.etjumlahUang.text.toString(),
                            binding.etTanggungan.text.toString(),
                            flagJenisZakat,
                            flagJenisSumbangan.toString(),
                            binding.etjumlahUangSumbangan.text.toString()
                        )
                    }
                } else {
                    binding.buttonUpload.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonUpload.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.red
                        )
                    )
                    viewModel.sendZakat(
                        getFile as File,
                        timeStamp,
                        userPreferences.getId().toString(),
                        binding.etjumlahUang.text.toString(),
                        binding.etTanggungan.text.toString(),
                        flagJenisZakat,
                        flagJenisSumbangan.toString(),
                        binding.etjumlahUangSumbangan.text.toString()
                    )
                }


            }

        }

        viewModel.result.observe(this) {
            when (it) {
                "sukses" -> {
                    Toast.makeText(
                        this@ZakatFitrahActivity,
                        "Data Berhasil Terkirim",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@ZakatFitrahActivity, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                else -> {
                    Toast.makeText(this@ZakatFitrahActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addZakat(
        file: File,
        tanggal: String,
        id_pembayar: String,
        pembayaran_uang: String,
        tanggungan: String,
        kode: Int
    ) {

//        val TANGGAL = tanggal.toRequestBody("text/plain".toMediaType())
//        val KODE= kode.toString().toRequestBody("text/plain".toMediaType())
//        val ID_PEMBAYAR = id_pembayar.toRequestBody("text/plain".toMediaType())
//        val Pembayaran_uang = pembayaran_uang.toRequestBody("text/plain".toMediaType())
//        val Tanggungan = tanggungan.toRequestBody("text/plain".toMediaType())
//        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
//            "file",
//            file.name,
//            requestImageFile
//        )
//        val client =ApiConfig.getApiService().addZakat(imageMultipart,TANGGAL,ID_PEMBAYAR,Pembayaran_uang,Tanggungan,KODE)
//        client.enqueue(object : Callback<RegisterResponse> {
//            override fun onResponse(
//                call: Call<RegisterResponse>,
//                response: Response<RegisterResponse>
//            ) {
//                binding.progressBar.visibility = View.GONE
//                val responseBody = response.body()
//                if (response.isSuccessful && responseBody != null) {
//                    Toast.makeText(this@ZakatFitrahActivity,responseBody.status,Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this@ZakatFitrahActivity, DashboardActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
//
//                } else {
//                    Toast.makeText(this@ZakatFitrahActivity,"gagal",Toast.LENGTH_SHORT).show()
//                    binding.buttonUpload.isEnabled = false
//
//                    binding.buttonUpload.setBackgroundDrawable(R.drawable.bg_button as Drawable)
//                }
//            }
//            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
//
//                Toast.makeText(this@ZakatFitrahActivity,t.message,Toast.LENGTH_SHORT).show()
//                binding.buttonUpload.isEnabled = true
//
//                binding.buttonUpload.setBackgroundColor(ContextCompat.getColor(this@ZakatFitrahActivity,R.color.red))
//            }
//        })
    }


    val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImg: Uri = data?.data as Uri
                binding.gambar.setImageURI(selectedImg)
                val myFile = uriToFile(selectedImg, this@ZakatFitrahActivity)
                getFile = myFile
            } else {
                Toast.makeText(this@ZakatFitrahActivity, "error", Toast.LENGTH_SHORT)
            }
        }

    private fun startGallery() {
        if (!areStoragePermissionsGranted()) {
            val androidVersion = Build.VERSION.SDK_INT
            if (androidVersion <= Build.VERSION_CODES.Q && androidVersion >= Build.VERSION_CODES.N) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_STORAGE
                )

            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }

        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getImageLauncher.launch(intent)
        }
    }

    private fun areStoragePermissionsGranted(): Boolean {

        val androidVersion = Build.VERSION.SDK_INT

        if (androidVersion <= Build.VERSION_CODES.Q && androidVersion >= Build.VERSION_CODES.N) {
            val readPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            return readPermission == PackageManager.PERMISSION_GRANTED
        } else {
            val readPermissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            return readPermissionCheck == PackageManager.PERMISSION_GRANTED
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Izin diberikan
                    startGallery()
                } else {
                    // Izin ditolak
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
                    }
                    val dialog = dialogBuilder.create()
                    dialog.show()
                }
            }
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startGallery()
            } else {
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
                }
                val dialog = dialogBuilder.create()
                dialog.show()
            }
        }


    private fun upload() {

        val userPreferences = UserPreferences(this@ZakatFitrahActivity)
        addZakat(
            getFile as File,
            timeStamp,
            userPreferences.getId().toString(),
            binding.etjumlahUang.text.toString(),
            binding.etTanggungan.text.toString(),
            flagJenisZakat
        )

    }

    private fun checkField(): Boolean {
        var result = false
        when {
            flagJenisZakat == 0 -> {
                Toast.makeText(
                    this@ZakatFitrahActivity,
                    "Tentukan Pilihan Zakat Terlebih Dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                result = true

            }

            getFile == null -> {
                Toast.makeText(
                    this@ZakatFitrahActivity,
                    "Masukan Bukti Transfer Terlebih Dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                result = true
            }

            binding.etTanggungan.text.toString().isBlank() -> {
                binding.etTanggungan.setError("Harap masuka jumlah tanggungan")
                result = true
            }

            binding.etjumlahUang.text.toString().isEmpty() -> {
                binding.etjumlahUang.setError("Harap masukan jumlah uang")
                result = true
            }

        }

        return result
    }

    private fun checkFieldSumbangan(): Boolean {
        var result = false
        when {
            binding.etjumlahUangSumbangan.text.toString().isBlank() -> {
                binding.etjumlahUangSumbangan.setError("Harap Masukan Jumlah Sumbangan")
                result = true
            }

            flagJenisSumbangan == 0 -> {
                Toast.makeText(
                    this@ZakatFitrahActivity,
                    "Tentukan Pilihan sumbangan Terlebih Dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                result = true
            }
        }
        return result
    }


}