package com.chillminds.local_construction.repositories.remote

data class Resource<out T>(val status: ApiCallStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> =
            Resource(status = ApiCallStatus.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(status = ApiCallStatus.ERROR, data = data, message = message)

        fun <T> loading(): Resource<T> =
            Resource(status = ApiCallStatus.LOADING, data = null, message = "Loading..!")
    }
}
