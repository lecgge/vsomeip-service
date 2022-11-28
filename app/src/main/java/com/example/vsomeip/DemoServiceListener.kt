package com.example.vsomeip

import android.util.Log

class DemoServiceListener : VSomeIPService.IServiceListener {

    companion object {
        const val TAG = "DemoServiceListener"
    }

    override fun onMessage(
        serviceId: Int,
        instanceId: Int,
        methodId: Int,
        clientId: Int,
        payload: ByteArray?
    ) {
        Log.d(TAG,
            "onMessage: serviceId: " + Integer.toHexString(serviceId) + ", instanceId: " + Integer.toHexString(
                instanceId
            ) +
                    ", methodId: " + Integer.toHexString(methodId) + ", clientId: " + Integer.toHexString(
                clientId
            ) + ", msgBytes: " + payload?.let {
                bytes2hex(
                    it
                )
            })
        Log.d("point","${MainActivity.vSomeIPService.mNativePtr}")
        try {
            if (methodId == 0x01) {
                MainActivity.vSomeIPService.sendResponse(
                    serviceId,
                    instanceId,
                    methodId,
                    byteArrayOf(0x10, 0x20, 0x30, 0x0f,0x44,0x43),
                    MainActivity.vSomeIPService.mNativePtr
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    // 输出十六进制字符串
    fun bytes2hex(bytes: ByteArray): String? {
        val sb = StringBuilder()
        var tmp: String? = null
        for (b in bytes) {
            tmp = Integer.toHexString(0xFF and b.toInt())
            if (tmp.length == 1) {
                tmp = "0$tmp"
            }
            sb.append(tmp)
            sb.append(" ")
        }
        return sb.toString()
    }
}