package com.app.contactlistapplication.retrofit

import com.app.contactlistapplication.model.ContactMain
import com.app.contactlistapplication.model.StarMain
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("iie-service/v1/contacts?")
    suspend fun getContactList1(@Query("pageNumber") pageNumber: Int) : ContactMain

    @POST("iie-service/v1/star/{id}")
    suspend fun setStar(@Path("id") userId: Int) : StarMain

    @POST("iie-service/v1/unstar/{id}")
    suspend fun setUnStar(@Path("id") userId: Int) : StarMain

}