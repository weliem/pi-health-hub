package com.welie.healthhub.fhir

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody
import okio.IOException

class FhirUploader {
    private val client = OkHttpClient()

    fun upload(fhir: String) {
        val body: RequestBody = fhir.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://10.0.0.59:8080/fhir/Observation")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("FHIR post FAILED")
            } else {
                println("FHIR post succeeded")
            }

            println(response.body!!.string())
        }
    }
}