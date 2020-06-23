package com.blank.firestorefirebase.data.model

data class Users(
    var id: String = "",
    var gender: String = "",
    var nama: String = "",
    var umur: Int = 0,
    var friends: MutableList<String> = mutableListOf()
)