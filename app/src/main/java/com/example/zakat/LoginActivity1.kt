package com.example.zakat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.zakat.databinding.ActivityMainBinding
import com.example.zakat.model.LoginResponse
import com.example.zakat.retrofit.ApiConfig
import com.example.zakat.sharedpreferences.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root);
        val userPreferences = UserPreferences(this@LoginActivity1);


        if (userPreferences.getId() != 0 ){
            val moveIntent = Intent(this@LoginActivity1, DashboardActivity::class.java)
            startActivity(moveIntent)
        }
        binding.btLogin.setOnClickListener{view->
            Toast.makeText(this@LoginActivity1,"tunggu",Toast.LENGTH_SHORT).show()
            cekLogin(binding.etUsername.text.toString(),binding.etPassword.text.toString())
        }
        binding.buttonRegister.setOnClickListener { view ->
            val moveIntent = Intent(this@LoginActivity1, RegisterActivity::class.java)
            startActivity(moveIntent)
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

                    val userPreferences = UserPreferences(this@LoginActivity1);
                    userPreferences.setId(responseBody.id)
                    val intent = Intent(this@LoginActivity1, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LoginActivity1,responseBody?.message,Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity1,t.message,Toast.LENGTH_SHORT).show()
            }
        })
    }
    }
