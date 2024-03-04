package com.ipocket.wallet.api

class Respbody<T> {
    public var msg: String = "data fetch failed"
    public var data: T? = null
    public var code: Int = 0
    public var status: Int? = 0
    override fun toString(): String {
        return "Respbody(msg='$msg', data=$data, code=$code, status=$status)"
    }
}