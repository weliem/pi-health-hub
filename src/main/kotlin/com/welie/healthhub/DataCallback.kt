package com.welie.healthhub

import com.welie.blessed.BluetoothPeripheral
import com.welie.healthhub.gatt.*

interface DataCallback {
    fun onSimpleObservation(observation: Observation)
    fun onObservationList(observationList: List<Observation>)
    fun onIntermediateCuffPressure(measurement: BloodPressureMeasurement, peripheral: BluetoothPeripheral)
    fun onBloodPressureFeature(measurement: BloodPressureFeature, peripheral: BluetoothPeripheral)
    fun onHeartRate(measurement: HeartRateMeasurement, peripheral: BluetoothPeripheral)
    fun onBloodOxygen(measurement: PulseOximeterSpotMeasurement, peripheral: BluetoothPeripheral)
    fun onBloodGlucose(measurement: GlucoseMeasurement, peripheral: BluetoothPeripheral)
    fun onAirPressure(pressure: Float, peripheral: BluetoothPeripheral)
}