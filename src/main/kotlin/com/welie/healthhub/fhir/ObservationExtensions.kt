package com.welie.healthhub.fhir

import com.welie.healthhub.observations.*

fun Observation.asFhir(): String {
    val observationTypeMdc = mdcObservationType()
    val observationUnitMdc = unit.mdc
    return "code: {\"coding\": [{\"system\": \"urn:iso:std:iso:11073:10101\",\"code\": \"$observationTypeMdc\" }],"
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