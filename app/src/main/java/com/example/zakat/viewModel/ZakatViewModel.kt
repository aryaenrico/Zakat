package com.example.zakat.viewModel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zakat.model.RegisterResponse
import com.example.zakat.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ZakatViewModel :ViewModel() {

    private var _loading =MutableLiveData<Boolean>()
    private var _tambahan =MutableLiveData<Boolean>( false)
    public val tambahan =_tambahan

    private var _result = MutableLiveData<String>()
    val result = _result
      val loading =_loading


    fun setTambahan(param:Boolean){

        _tambahan.value = param
    }

    fun sendZakat(file:File,tanggal:String,id_pembayar:String,pembayaran_uang:String,tanggungan:String,kode:Int,kode_sumbangan:String,total_sumbangan:String){
        _loading.value = true
        val TANGGAL = tanggal.toRequestBody("text/plain".toMediaType())
        val KODE= kode.toString().toRequestBody("text/plain".toMediaType())
        val KODE_SUMBANGAN= kode_sumbangan.toString().toRequestBody("text/plain".toMediaType())
        val total_SUMBANGAN= total_sumbangan.toString().toRequestBody("text/plain".toMediaType())
        val ID_PEMBAYAR = id_pembayar.toRequestBody("text/plain".toMediaType())
        val Pembayaran_uang = pembayaran_uang.toRequestBody("text/plain".toMediaType())
        val Tanggungan = tanggungan.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().addZakat(imageMultipart,TANGGAL,ID_PEMBAYAR,Pembayaran_uang,Tanggungan,KODE,KODE_SUMBANGAN,total_SUMBANGAN)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _loading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _result.value = responseBody.status

                } else {
                    _result.value ="gagal";

                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _result.value = t.message

            }
        })
    }
}