package com.blank.firestorefirebase.data.model

data class Users(
    var id: String = "",
    var gender: String = "",
    var username: String = "",
    var umur: Int = 0,
    var tokenNotif: String = "",
    var friends: MutableList<String> = mutableListOf()
)