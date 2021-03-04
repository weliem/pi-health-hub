package com.welie.healthhub.observations

import java.util.*

data class Observation(
    val value: Float,
    val type: ObservationType,
    val unit: ObservationUnit,
    val timestamp: Date?,
    val location: ObservationLocation,
    val sensorType: SensorType,
    val userId: Int? = null,
    val receivedTimestamp: Date,
    val systemId: String
)