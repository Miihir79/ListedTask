package com.mihir.listedtask.data.model

import androidx.annotation.Keep

@Keep
data class Data(
    val overall_url_chart: Map<String, Int>?,
    val recent_links: List<LinkItem>?,
    val top_links: List<LinkItem>?
)