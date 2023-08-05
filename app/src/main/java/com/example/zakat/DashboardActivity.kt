package com.example.zakat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zakat.R
import com.example.zakat.databinding.ActivityDashboardBinding
import com.example.zakat.databinding.ActivityMainBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root);

        binding.ivZakat.setOnClickListener{
            val moveIntent = Intent(this@DashboardActivity, ZakatFitrahActivity::class.java)
            startActivity(moveIntent)
        }
        binding.imageView5.setOnClickListener {
            val moveIntent = Intent(this@DashboardActivity, History_Activity::class.java)
            startActivity(moveIntent)
        }
    }
}