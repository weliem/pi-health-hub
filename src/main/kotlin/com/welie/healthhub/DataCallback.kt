package com.welie.healthhub

import com.welie.healthhub.observations.Observation
import java.util.*

interface DataCallback {
    fun onObservationList(observationList: List<Observation>)
    fun onBatteryPercentage(percentage: Int, systemId: String)
    fun onPeripheralTime(dateTime: Date, systemId: String)
    fun onManufacturerName(manufacturer: String, systemId: String)
    fun onModelNumber(modelNumber: String, systemId: String)
    fun onSoftwareRevision(softwareRevision: String, systemId: String)
    fun onHardwareRevision(hardwareRevision: String, systemId: String)
    fun onFirmwareRevision(firmwareRevision: String, systemId: String)
    fun onSerialNumber(serialNumber: String, systemId: String)
}