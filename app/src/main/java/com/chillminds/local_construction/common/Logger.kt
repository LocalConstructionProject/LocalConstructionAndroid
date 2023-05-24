package com.chillminds.local_construction.common

import android.util.Log
import com.chillminds.local_construction.BuildConfig

class Logger {
    companion object {
        fun error(title: String, message: String) {
            if (BuildConfig.DEBUG) {
                Log.e(title, message)
            }
        }

        fun error(title: String, message: String, exception: Exception?) {
            if (BuildConfig.DEBUG) {
                Log.e(title, message, exception)
            }
        }

        fun info(title: String, message: String) {
            if (BuildConfig.DEBUG) {
                Log.i(title, message)
            }
        }

        fun verbose(title: String, message: String) {
            if (BuildConfig.DEBUG) {
                Log.v(title, message)
            }
        }

        fun warn(title: String, message: String) {
            if (BuildConfig.DEBUG) {
                Log.w(title, message)
            }
        }
    }
}