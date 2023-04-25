package com.mihir.listedtask.data.model

import androidx.annotation.Keep

@Keep
data class InfoTileData(
    val resID: Int,
    val titleText: String,
    val subText: String
)
