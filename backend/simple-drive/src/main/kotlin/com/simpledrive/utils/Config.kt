package com.simpledrive.utils

object Config {
    val FILE_STORE = load("FILE_STORE", "LOCAL")
    val FILE_STORE_DIR_PATH = load("FILE_STORE_DIR_PATH", "../local_data")
    val MONGODB_HOST = load("MONGODB_HOST", "localhost")
    val MONGODB_PORT = load("MONGODB_PORT", 27017)
    val MONGODB_USERNAME = load("FILE_STORE", "admin")
    val MONGODB_PASSWORD = load("FILE_STORE", "admin")
    val JWT_SECRET = load("JWT_SECRET", "my-secret")
    val JWT_ISSUER = load("JWT_ISSUER", "simpledrive-backend")
    val ACCESS_AUD = load("ACCESS_AUD", "simledrive-access")
    val REFRESH_AUD = load("REFRESH_AUD", "simpledrive-refresh")
    private fun load(key: String, defaultValue: Int): Int {
        return System.getenv(key)?.toInt() ?: defaultValue
    }
    private fun load(key: String, defaultValue: String?): String? {
        return System.getenv(key) ?: defaultValue
    }
}