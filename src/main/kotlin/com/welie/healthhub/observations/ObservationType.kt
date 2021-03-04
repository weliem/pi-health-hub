package com.welie.healthhub.observations

enum class ObservationType(val mdc: String, val loinc: String) {
    BodyFatPercentage("",""),
    BodyWeight("MDC_MASS_BODY_ACTUAL", "3141-9"),
    BodyHeight("MDC_LEN_BODY_ACTUAL", "8302-2"),
    BodyMassIndex("MDC_RATIO_MASS_BODY_LEN_SQ", ""),
    BodyTemperature("MDC_TEMP_BODY", "8310-5"),
    BloodGlucoseConcentration("MDC_CONC_GLU_GEN", "2339-0"),
    BloodOxygenSaturation("MDC_PULS_OXIM_SAT_O2", "20564-1"),
    DiastolicPressure("MDC_PRESS_BLD_NONINV_DIA", "8462-4"),
    HeartRate("MDC_ECG_HEART_RATE", "8867-4"),
    MeanArterialPressure("MDC_PRESS_BLD_NONINV_MEAN", "8478-0"),
    PulseAmplitudeIndex("MDC_SAT_O2_QUAL",""),
    SystolicPressure("MDC_PRESS_BLD_NONINV_SYS", "8480-6"),
    Temperature("MDC_TEMP", "35095-9"),
}