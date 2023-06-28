package com.example.zakat.model

import com.google.gson.annotations.SerializedName

class RegisterResponse (
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("messgae")
    val message: String,
)