package com.example.zakat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zakat.databinding.ActivityMainBinding
import com.example.zakat.model.LoginResponse
import com.example.zakat.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root);
        binding.btLogin.setOnClickListener{view->
            Toast.makeText(this@LoginActivity1,"tunggu",Toast.LENGTH_SHORT).show()
            cekLogin(binding.etUsername.text.toString(),binding.etPassword.text.toString())
        }
    }

    private fun cekLogin(username:String,password:String){
        val client = ApiConfig.getApiService().login(username, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                   Toast.makeText(this@LoginActivity1,responseBody.status,Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity1,responseBody?.message,Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                Toast.makeText(this@LoginActivity1,"ancur servernya",Toast.LENGTH_SHORT).show()
            }
        })
    }
    }