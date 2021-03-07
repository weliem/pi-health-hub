package com.welie.healthhub.observations

import java.util.*

data class Observation(
    val value: Float,
    val unit: ObservationUnit,
    val duration: Float? = null,
    val subject: ObservationSubject,
    val quantityType: QuantityType,
    val volumeOf: VolumeTypes? = null,
    val timestamp: Date?,
    val location: ObservationLocation,
    val sensorType: SensorType,
    val userId: Int? = null,
    val receivedTimestamp: Date,
    val systemInfo: SystemInfo
)