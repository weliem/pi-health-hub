package com.welie.healthhub

import com.welie.healthhub.gatt.ObservationUnit
import java.util.*

data class Observation(
    val value: Float?,
    val type: ObservationType,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val userId: Int?,
    val receivedTimestamp: Date,
    val systemId: String
)