package com.welie.healthhub.observations

object SystemInfoStore {
    private val store = HashMap<String, SystemInfo>()

    fun get(systemId: String) : SystemInfo {
        if (!store.containsKey(systemId)) store[systemId] = SystemInfo(systemId)
        return requireNotNull(store[systemId])
    }

    fun add(systemInfo: SystemInfo) {
        store[systemInfo.systemId] = systemInfo
    }
}