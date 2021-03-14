package com.welie.healthhub.observations

import com.welie.healthhub.observations.Observation
import java.util.*

interface ObservationsCallback {
    /**
     * List of observations received
     */
    fun onObservationList(observationList: List<Observation>)
}