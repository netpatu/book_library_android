package com.ipocket.wallet.api

class RespEmptyPayload {
    public var msg:String = "data fetch failed"
    public var code:Int = 0
    public var status:String? = "0"
    override fun toString(): String {
        return "Respbody(msg='$msg', code=$code, status=$status)"
    }
}