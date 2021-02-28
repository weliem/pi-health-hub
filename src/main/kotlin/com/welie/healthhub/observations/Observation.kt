package com.welie.healthhub.observations

import java.util.*

data class Observation(
    val value: Float,
    val type: ObservationType,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val location: ObservationLocation,
    val userId: Int?,
    val status: List<ObservationStatus>,
    val receivedTimestamp: Date,
    val systemId: String
)