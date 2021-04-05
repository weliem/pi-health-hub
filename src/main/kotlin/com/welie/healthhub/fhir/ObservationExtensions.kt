package com.welie.healthhub.fhir

import com.welie.healthhub.observations.*
import kotlinx.serialization.json.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Observation.asFhir(): String {
    val codingSystem = "urn:iso:std:iso:11073:10101"
    val observationCode = mdcObservationType()
    val observationDisplay = mdcObservationDisplay(observationCode)
    val unitCode = unit.mdc
    val localDate = timestamp!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val dateTime = DateTimeFormatter.ISO_DATE_TIME.format(localDate)

    val fhir = buildJsonObject {
        put("resourceType", "Observation")
        put("status", "final")
        putJsonArray("category") {
            addJsonObject {
                putJsonArray("coding") {
                    addJsonObject {
                        put("system", "http://terminology.hl7.org/CodeSystems/observation_category")
                        put("code", "vital-signs")
                        put("display", "Vital Signs")
                    }
                }
            }
        }
        putJsonObject("code") {
            putJsonArray("coding") {
                addJsonObject {
                    put("system", codingSystem)
                    put("code", observationCode)
                    put("display", observationDisplay)
                }
            }
        }
        put("effectiveDateTime", dateTime)
        putJsonObject("valueQuantity") {
            put("value", value)
            put("unit", unit.notation)
            put("system", codingSystem)
            put("code", unitCode)
        }
    }
    return fhir.toString()
}

fun Observation.mdcObservationType(): String {
    if (quantityType == QuantityType.Temperature) {
        return if(subject == ObservationSubject.Body) {
            when(location) {
                ObservationLocation.Ear -> "MDC_TEMP_EAR"
                ObservationLocation.Armpit -> "MDC_TEMP_AXILLA"
                ObservationLocation.Finger -> "MDC_TEMP_FINGER"
                ObservationLocation.GastroIntestinalTract -> "MDC_TEMP_GIT"
                ObservationLocation.Mouth -> "MDC_TEMP_ORAL"
                ObservationLocation.Rectum -> "MDC_TEMP_RECT"
                ObservationLocation.Toe -> "MDC_TEMP_TOE"
                ObservationLocation.Tympanum -> "MDC_TEMP_TYMP"
                else -> "MDC_TEMP_BODY"
            }
        } else {
            "MDC_TEMP"
        }
    }

    if (quantityType == QuantityType.Mass && subject == ObservationSubject.Body) {
        return "MDC_MASS_BODY_ACTUAL"
    }

    if (quantityType == QuantityType.Frequency && subject == ObservationSubject.HeartBeat) {
        return when(sensorType) {
            SensorType.EcgSensor -> "MDC_ECG_HEART_RATE"
            SensorType.PressureCuff -> "MDC_PULS_RATE_NON_INV"
            SensorType.PpgSensor -> "MDC_PLETH_PULS"
            else -> "MDC_HF_HR"
        }
    }

    if (quantityType == QuantityType.SystolicPressure) {
        return "MDC_PRESS_BLD_NONINV_SYS"
    }

    if (quantityType == QuantityType.DiastolicPressure) {
        return "MDC_PRESS_BLD_NONINV_DIA"
    }

    if (quantityType == QuantityType.MeanPressure) {
        return "MDC_PRESS_BLD_NONINV_MEAN"
    }

    return ""



//    BodyHeight("MDC_LEN_BODY_ACTUAL", "8302-2"),
//    BodyMassIndex("MDC_RATIO_MASS_BODY_LEN_SQ", ""),
//    BloodGlucoseConcentration("MDC_CONC_GLU_GEN", "2339-0"),
//    BloodOxygenSaturation("MDC_PULS_OXIM_SAT_O2", "20564-1")
//    PulseAmplitudeIndex("MDC_SAT_O2_QUAL",""),
}

fun mdcObservationDisplay(mdcCode : String) : String {
    return when(mdcCode) {
        "MDC_TEMP_TYMP" -> "Temperature tympanum"
        else -> "unknown"
    }
}
