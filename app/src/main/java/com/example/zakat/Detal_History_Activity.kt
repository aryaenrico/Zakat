package com.example.zakat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.zakat.databinding.ActivityDetalHistoryBinding
import com.example.zakat.model.DaftarTransfer
import java.text.NumberFormat
import java.util.Locale
import java.util.StringTokenizer

class Detal_History_Activity : AppCompatActivity() {
    private lateinit var binding :ActivityDetalHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val param = intent.getParcelableExtra<DaftarTransfer>("transfer") as DaftarTransfer

        binding.tvTglBayar.text = param?.tgl_penyerahan
        binding.tvJumlah.text = token(currency(param.total_pembayaran.toInt()))

        Glide.with(this@Detal_History_Activity)
            .load("https://indrasela.net/mobile_zakat/foto/${param?.foto}")
            .into(binding.gambar)

        binding.tvStatus.text = param?.status
    }

    private fun currency(data:Int):String{
        val locale = Locale("IND", "ID")
        val formatter = NumberFormat.getCurrencyInstance(locale)
        return formatter.format(data)
    }

    private fun token(data: String): String {
        val tokenizer = StringTokenizer(data, ",")
        return tokenizer.nextToken()
    }
}