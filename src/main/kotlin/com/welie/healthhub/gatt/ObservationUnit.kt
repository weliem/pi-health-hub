package com.welie.healthhub.gatt

enum class ObservationUnit(val notation: String) {
    MMHG("mmHg"),
    KPA("Kpa"),
    MiligramPerDeciliter("mg/dL"),
    MmolPerLiter("mmol/L"),
    Celsius("\u00B0C"),
    Fahrenheit("\u00B0F"),
    Kilograms("Kg"),
    Pounds("lbs")
}