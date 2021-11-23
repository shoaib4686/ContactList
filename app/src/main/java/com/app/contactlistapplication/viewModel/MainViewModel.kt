package com.app.contactlistapplication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.contactlistapplication.retrofit.ApiResponse
import com.app.contactlistapplication.repository.MainRepository
import kotlinx.coroutines.Dispatchers


class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getContactList1(pageNumber: Int) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = mainRepository.getContactList(pageNumber)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setStar(userId: Int) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = mainRepository.setStar(userId)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun setUnStar(userId: Int) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(data = null))
        try {
            emit(ApiResponse.success(data = mainRepository.setUnStar(userId)))
        } catch (exception: Exception) {
            emit(ApiResponse.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}