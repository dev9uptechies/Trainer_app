package com.example.model.HomeFragment

data class NewsModel(
    val data: MutableList<Data>,
    val message: String,
    val status: Boolean
){
    data class Data(
        val created_at: String,
        val deleted_at: Any,
        val description: String,
        val id: Int,
        val image: String,
        val title: String,
        val updated_at: String
    )
}