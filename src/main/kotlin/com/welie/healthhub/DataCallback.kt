package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.gatt.*
import com.welie.healthhub.observations.Observation

interface DataCallback {
    fun onObservationList(observationList: List<Observation>)
    fun onIntermediateCuffPressure(measurement: BloodPressureMeasurement, peripheral: BluetoothPeripheral)
    fun onBloodPressureFeature(measurement: BloodPressureFeature, peripheral: BluetoothPeripheral)
    fun onAirPressure(pressure: Float, peripheral: BluetoothPeripheral)
}