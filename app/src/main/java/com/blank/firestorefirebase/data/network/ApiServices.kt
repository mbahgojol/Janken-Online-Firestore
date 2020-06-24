package com.blank.chapter9

import com.blank.firestorefirebase.data.model.PayloadNotif
import com.blank.firestorefirebase.utils.AppConstant.SEND_NOTIF
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServices {
    @POST(SEND_NOTIF)
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun pushNotif(
        @Header("Authorization") token: String,
        @Body payloadNotif: PayloadNotif
    ): Single<String>
}