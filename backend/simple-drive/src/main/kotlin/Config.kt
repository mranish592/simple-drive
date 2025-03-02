package com.simpledrive

object Config {


    fun load(key: String, defaultValue: Int): Int {
        return System.getenv(key)?.toInt() ?: defaultValue
    }
    fun load(key: String, defaultValue: String?): String? {
        return System.getenv(key) ?: defaultValue
    }
}