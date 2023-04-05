package dev.abdujabbor.chatappsimple.retrofit

import dev.abdujabbor.chatappsimple.constants.Constants.CONTENT_TYPE
import dev.abdujabbor.chatappsimple.constants.Constants.SERVER_KEY
import dev.abdujabbor.chatappsimple.models.notifiation.PushNotifiation
import dev.abdujabbor.chatappsimple.utils.MyData
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface NotificationAPI {

    //@Headers("Authorization: key=$SERVER_KEY", "Content-Type:${CONTENT_TYPE}",)
    @Headers("Authorization: key=AAAAONZpshE:APA91bGFO8oS8e-IlpRPSkC15mLd9HLIUpzjld8u4NCpnGWpj216jqNMfe3Sn3hIHWvOphLaoQaLNp44qi78eph2cVD0_TGcjRTY9l5OW8pyxnWygsJblSnZRhdiZh7U4WVMujobEhED",
        "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotifiation
    ): Response<ResponseBody>
}