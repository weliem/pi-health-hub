package com.welie.healthhub

enum class TemperatureType(val value: Int) {
    Armpit(1), Body(2), Ear(3), Finger(4), GastroIntestinalTract(5), Mouth(6), Rectum(7), Toe(8), Tympanum(9);

    companion object {
        fun fromValue(value: Int): TemperatureType? {
            for (type in values()) {
                if (type.value == value) return type
            }
            return null
        }
    }
}