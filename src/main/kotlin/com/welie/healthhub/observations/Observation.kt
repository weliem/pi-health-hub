package com.welie.healthhub.observations

import java.util.*

data class Observation(
    val value: Float?,
    val type: ObservationType,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val location: ObservationLocation,
    val userId: Int?,
    val receivedTimestamp: Date,
    val systemId: String
)