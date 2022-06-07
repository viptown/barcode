package com.example.barcodescanner.utils

object Constants{
    const val TAG:String = "로그"
}

enum class SEARCH_TYPE{
    PHOTO,
    USER
}

object API{
    const val BASE_URL :String = "https://api.unsplash.com/"
    const val CLIENT_ID :String = ""

    const val SEARCH_PHOTO: String = "search/photos"
    const val SEARCH_USERS: String = "search/users"
    const val SEARCH_LOCATIONS: String = "search/locations"
}