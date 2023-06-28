package com.example.zakat.model

import com.google.gson.annotations.SerializedName

class LoginResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("messgae")
    val message: String,

    @field:SerializedName("id")
    val id: Int,
)