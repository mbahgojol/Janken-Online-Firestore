package com.blank.firestorefirebase.data.model

data class Model(
    var boolean: Boolean = false,
    var country: String = "",
    var level: Level? = null,
    var name: String = "",
    var population: Int = 0,
    var state: String = "",
    var regions: ArrayList<String> = arrayListOf()
)

data class Level(
    var isAsset: Boolean = false,
    var point: Int = 0
)