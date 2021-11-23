package com.app.contactlistapplication.repository

import com.app.contactlistapplication.retrofit.ApiHelper


class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getContactList(pageNumber: Int) = apiHelper.getContactList1(pageNumber)

    suspend fun setStar(userId: Int) = apiHelper.setStarred(userId)

    suspend fun setUnStar(userId: Int) = apiHelper.setUnStarred(userId)

}