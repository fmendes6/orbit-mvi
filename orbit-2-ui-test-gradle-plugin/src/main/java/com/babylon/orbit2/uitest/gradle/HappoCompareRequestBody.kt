package com.babylon.orbit2.uitest.gradle

import com.google.gson.annotations.SerializedName

internal data class HappoCompareRequestBody(
    @SerializedName("link")
    val prLink: String
)