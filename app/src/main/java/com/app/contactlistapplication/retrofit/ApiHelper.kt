package com.app.contactlistapplication.retrofit

import com.app.contactlistapplication.model.ContactMain
import com.app.contactlistapplication.model.StarMain

class ApiHelper(private val apiService: ApiService) {

    suspend fun getContactList1(pageNumber: Int): ContactMain {
        return apiService.getContactList1(pageNumber)
    }

    suspend fun setStarred(userId: Int): StarMain {
        return apiService.setStar(userId)
    }

    suspend fun setUnStarred(userId: Int): StarMain {
        return apiService.setUnStar(userId)
    }

}