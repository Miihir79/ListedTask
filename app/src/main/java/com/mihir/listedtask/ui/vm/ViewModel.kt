package com.mihir.listedtask.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mihir.listedtask.BuildConfig
import com.mihir.listedtask.common.API_FAILED
import com.mihir.listedtask.common.AppObjectController
import com.mihir.listedtask.common.NO_NETWORK
import com.mihir.listedtask.common.Utils
import com.mihir.listedtask.data.model.DashBoardResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ViewModel(application: Application): AndroidViewModel(application) {

    var resp = MutableLiveData<DashBoardResponse>()
    var apiStatus = MutableSharedFlow<String?>(replay = 0)

    init {
        getDashBoardData()
    }

    fun getDashBoardData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (Utils.isNetworkConnected(getApplication())) {
                    val response = AppObjectController.service.getDashBoardData("Bearer " + BuildConfig.TOKEN)
                    if (response.isSuccessful) {
                        MainScope().launch {
                            response.body()?.let { resp.value = it }
                        }
                    } else {
                        apiStatus.emit(API_FAILED)
                    }
                } else {
                    apiStatus.emit(NO_NETWORK)
                }

            } catch (e:Exception) {
                e.printStackTrace()
                apiStatus.emit(API_FAILED)
            }

        }
    }
}