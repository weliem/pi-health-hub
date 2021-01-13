interface DataCallback {
    fun onTemperature(measurement : TemperatureMeasurement)
    fun onBloodPressure(measurement: BloodPressureMeasurement)
    fun onWeight(measurement: WeightMeasurement)
    fun onHeartRate(measurement: HeartRateMeasurement)
    fun onBloodOxygen(measurement: PulseOximeterSpotMeasurement)
}