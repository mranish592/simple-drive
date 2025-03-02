package com.simpledrive

object Config {
    val FILE_STORE = load("FILE_STORE", "LOCAL")

    fun load(key: String, defaultValue: Int): Int {
        return System.getenv(key)?.toInt() ?: defaultValue
    }
    fun load(key: String, defaultValue: String?): String? {
        return System.getenv(key) ?: defaultValue
    }
}