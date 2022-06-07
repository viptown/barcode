package com.example.barcodescanner.data.remote

object HttpRoutes {

    private const val BASE_URL = "http://haihuozhan.com:8083"
    const val POSTS = "$BASE_URL/getposition.php"
    const val PUTS = "$BASE_URL/pushposition.php"
//    private const val BASE_URL = "https://jsonplaceholder.typicode.com"
//    const val POSTS = "$BASE_URL/posts"
}