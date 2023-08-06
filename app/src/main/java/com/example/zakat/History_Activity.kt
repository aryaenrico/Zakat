package com.example.zakat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zakat.adapter.historyAdapter
import com.example.zakat.databinding.ActivityZakatFitrahBinding
import com.example.zakat.model.HistoryResponse
import com.example.zakat.retrofit.ApiConfig
import com.example.zakat.sharedpreferences.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class History_Activity : AppCompatActivity() {
    private lateinit var binding:ActivityZakatFitrahBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val userPreferences = UserPreferences(this@History_Activity);
        val client = ApiConfig.getApiService().getHistoryTranscaction(userPreferences.getId())
        client.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val rvHeroes = findViewById<RecyclerView>(R.id.rv_daftar_transfer)
                    rvHeroes.layoutManager = LinearLayoutManager(this@History_Activity)
                    rvHeroes.adapter = historyAdapter(responseBody.data)


                } else {

                }
            }
            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                Toast.makeText(this@History_Activity,t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }
}