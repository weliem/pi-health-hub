package com.welie.healthhub.observations

object SystemInfoStore {
    private val store = HashMap<String, SystemInfo>()

    fun get(systemId: String) : SystemInfo? {
        return store[systemId]
    }

    fun add(systemInfo: SystemInfo) {
        store[systemInfo.systemId] = systemInfo
    }
}