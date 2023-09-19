package com.s.newsapp.data.model

import java.io.Serializable

data class CategoryArticle(
    val id: String,
    val name: String,
    val description : String,
    val url :String,
    val category :String,
    val language:String,
    val country :String,

    ):Serializable