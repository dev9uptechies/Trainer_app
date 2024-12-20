package com.example.model.PrivacyPolicy

data class privacypolicy(
    val data: Data,
    val message: String,
    val status: Boolean
){
    data class Data(
        val created_at: Any,
        val description: String,
        val id: Int,
        val title: String,
        val updated_at: String
    )
}