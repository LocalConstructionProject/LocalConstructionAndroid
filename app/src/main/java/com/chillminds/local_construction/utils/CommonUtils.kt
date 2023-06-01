package com.chillminds.local_construction.utils

fun <A, B> Pair<A?, B?>.validate(): Pair<A, B>? =
    if (this.first != null && this.second != null) {
        Pair(this.first!!, this.second!!)
    } else {
        null
    }