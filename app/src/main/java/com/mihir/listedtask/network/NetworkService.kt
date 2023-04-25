package com.mihir.listedtask.network

import com.mihir.listedtask.data.model.DashBoardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface NetworkService {
    @GET("dashboardNew")
    suspend fun getDashBoardData(@Header("Authorization") token: String): Response<DashBoardResponse>
}