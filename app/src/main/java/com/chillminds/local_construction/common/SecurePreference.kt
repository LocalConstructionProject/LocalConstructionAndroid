package com.chillminds.local_construction.common

import android.content.SharedPreferences

/**
 * Wrapper Class for secure shared preference
 * this will helps to read and write data using encryption technique
 * @param preferences will inject by KOIN
 * @sample val sharedPreferences: SecurePreference by inject()
 * @see com.chillminds.tamilbible.AppModule class to check the encryption technique
 * @see com.chillminds.tamilbible.common.PreferenceKeys class for Shared Preference Keys
 * @author Yabaze T
 */
class SecurePreference(private val preferences: SharedPreferences) {

    fun readString(key: String, defaultValue: String): String? {
        return preferences.getString(key, defaultValue)
    }

    fun readInt(key: String, defaultValue: Int): Int {
        return preferences.getInt(key, defaultValue)
    }

    fun readFloat(key: String, defaultValue: Float): Float {
        return preferences.getFloat(key, defaultValue)
    }

    fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun readLong(key: String, defaultValue: Long): Long {
        return preferences.getLong(key, defaultValue)
    }

    fun setString(key: String, value: String) {
        with(preferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun setInt(key: String, value: Int) {
        with(preferences.edit()) {
            putInt(key, value)
            apply()
        }
    }

    fun setFloat(key: String, value: Float) {
        with(preferences.edit()) {
            putFloat(key, value)
            apply()
        }
    }

    fun setBoolean(key: String, value: Boolean) {
        with(preferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun setLong(key: String, value: Long) {
        with(preferences.edit()) {
            putLong(key, value)
            apply()
        }
    }
}