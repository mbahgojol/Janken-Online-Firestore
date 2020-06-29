package com.blank.firestorefirebase.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class NotifFriends(
    val data: Data? = null,
    val to: String? = null
) {

    @Parcelize
    data class Data(
        val idTarget: String? = null,
        val idPengirim: String? = null,
        val title: String? = null,
        val message: String? = null,
        val statusFriend: Boolean? = null,
        val type: String? = null
    ) : Parcelable
}