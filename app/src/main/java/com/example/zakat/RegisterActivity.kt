package com.example.zakat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zakat.databinding.ActivityMainBinding
import com.example.zakat.databinding.ActivityRegisterBinding
import com.example.zakat.model.LoginResponse
import com.example.zakat.model.RegisterResponse
import com.example.zakat.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding
    private  var flag:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonRegister.setOnClickListener { view->
           register(binding.etUsername.text.toString(),binding.editTextPassword.text.toString(),binding.etNama.text.toString(),binding.etAlamat.text.toString(),binding.etNoTelp.text.toString())
        }

    }

    private fun register(username:String,password:String,nama:String,alamat:String,no_telp:String){
        val client = ApiConfig.getApiService().register(username, password,nama,alamat, no_telp)
        var data:Int=0
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null && response.code() == 200) {
                    Toast.makeText(this@RegisterActivity,responseBody.status, Toast.LENGTH_SHORT).show()
                    val moveIntent = Intent(this@RegisterActivity, LoginActivity1::class.java)
                    startActivity(moveIntent)
                } else {
                    Toast.makeText(this@RegisterActivity,"Gagal membuat Akun ", Toast.LENGTH_SHORT).show()

                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                Toast.makeText(this@RegisterActivity,t.message, Toast.LENGTH_SHORT).show()

            }
        })

    }


}