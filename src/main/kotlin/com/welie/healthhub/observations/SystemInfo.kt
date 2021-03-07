package com.welie.healthhub.observations

import java.util.*

data class SystemInfo(val systemId: String) {
    var manufacturer = ""
    var model = ""
    var serialNumber = ""
    var firmwareVersion = ""
    var softwareVersion = ""
    var hardwareVersion = ""
    var batteryLevel = 100
    var dateTime: Date? = null
}