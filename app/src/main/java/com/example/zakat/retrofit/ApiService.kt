package com.example.zakat.retrofit

import com.example.zakat.model.HistoryResponse
import com.example.zakat.model.LoginResponse
import com.example.zakat.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("loginMuzakki.php")
    fun login(@Field("username") username: String,@Field("password") password: String):Call<LoginResponse>

    @FormUrlEncoded
    @POST("registerMuzakki.php")
    fun register(@Field("username") username: String,@Field("password") password: String,@Field("nama") nama: String,@Field("alamat") alamat: String,@Field("no_telp") no_telp: String):Call<RegisterResponse>

    @Multipart
    @POST("zakatFitrah.php")
    fun addZakat(
        @Part file: MultipartBody.Part,
        @Part("tanggal") description: RequestBody,
        @Part("id_pembayar") id_pembayar: RequestBody,
        @Part("pembayaran_uang") pembayaran_uang: RequestBody,
        @Part("tanggungan") tanggungan: RequestBody,
        @Part("kode") kode: RequestBody,
        @Part("kode_sumbangan") kode_sumbangan: RequestBody,
        @Part("total_tambahan") total_tambahan: RequestBody
    ):Call<RegisterResponse>

    @GET("status_pembayaran.php")
    fun getHistoryTranscaction(@Query ("id") id:Int):Call<HistoryResponse>
}