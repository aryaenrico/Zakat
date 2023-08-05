package com.example.zakat.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class HistoryResponse(
    @field:SerializedName("data")
    val data:ArrayList<DaftarTransfer>
)

@Parcelize
data class DaftarTransfer(
    @field:SerializedName("id_pembayaran")
    val id_pembayaran:Int,
    @field:SerializedName("id_pembayar")
    val id_pembayar:Int,
    @field:SerializedName("nama_pembayar")
    val nama_pembayar:String,
    @field:SerializedName("tipe_zakat")
    val tipe_zakat:String,
    @field:SerializedName("tgl_penyerahan")
    val tgl_penyerahan:String,
    @field:SerializedName("pembayaran_uang")
    val pembayaran_uang:Int,
    @field:SerializedName("jumlah_tanggungan")
    val jumlah_tanggungan:Int,
    @field:SerializedName("total_pembayaran")
    val total_pembayaran:String,
    @field:SerializedName("foto")
    val foto:String,
    @field:SerializedName("status")
    val status:String
): Parcelable

