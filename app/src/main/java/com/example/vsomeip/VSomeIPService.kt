package com.example.vsomeip

import android.util.Log

class VSomeIPService(var appName: String,var listener: IServiceListener) {

    init {
        create(appName,this)
    }

    private var startTask = StartTask(this)
    var mNativePtr : Long = 0L
    companion object{
        private val TAG = "SomeIPService"

        init {
            System.loadLibrary("ServiceJNI")
        }
    }


    external fun create(appName: String?, service: VSomeIPService)
    external fun init(): Boolean
    external fun offer_service(serviceId: Int, instanceId: Int)
    external fun offer_service1(serviceId: Int, instanceId: Int,methodId: Int)
    external fun offer_event(serviceId: Int, instanceId: Int, eventId: Int, eventGroupId: Int)
    external fun offer_field(serviceId: Int, instanceId: Int, fieldId: Int, fieldGroupId: Int)
    external fun stop_offer_event(serviceId: Int, instanceId: Int, eventId: Int)
    external fun stop_offer_field(serviceId: Int, instanceId: Int, fieldId: Int)
    external fun start()
    external fun stop()
    external fun notify(serviceId: Int, instanceId: Int, eventId: Int, payload: ByteArray?,clientId: Int)
    external fun send_response(serviceId: Int, instanceId: Int, methodId: Int, payload: ByteArray?,nativePtr : Long)
    external fun close()

    @JvmName("getAppName1")
    fun getAppName(): String {
        return appName
    }

    fun initialize(): Boolean {
        return init()
    }

    /*提供服务*/
    fun offerService(serviceId: Int, instanceId: Int) {
        offer_service(serviceId, instanceId)
    }

    /*提供事件*/
    fun offerEvent(serviceId: Int, instanceId: Int, eventId: Int, eventGroupId: Int) {
        offer_event(serviceId, instanceId, eventId, eventGroupId)
    }

    /*启动服务，启动run线程*/
    fun startService() {
        startTask.run()
    }

    /*停止服务*/
    fun stopService() {
        stop()
        close()
    }

    /*发送事件或属性消息*/
    fun notifyEvent(serviceId: Int, instanceId: Int, eventId: Int, payload: ByteArray?,clientId: Int) {
        notify(serviceId, instanceId, eventId, payload,clientId)
    }

    fun sendResponse(serviceId: Int, instanceId: Int, methodId: Int, payload: ByteArray?, nativePtr : Long) {
        send_response(serviceId, instanceId, methodId, payload,nativePtr)
    }

    fun onMessage(
        serviceId: Int,
        instanceId: Int,
        methodId: Int,
        clientId: Int,
        payload: ByteArray?
    ) {
        // 暂时不处理，直接回调给监听
        this.listener?.onMessage(serviceId, instanceId, methodId, clientId, payload)
    }

    /*服务监听接口*/
    interface IServiceListener {
        fun onMessage(
            serviceId: Int,
            instanceId: Int,
            methodId: Int,
            clientId: Int,
            payload: ByteArray?
        )
    }

    // 开启服务端
    internal class StartTask(s: VSomeIPService) : Thread() {
        private val service: VSomeIPService
        override fun run() {
            try {
                service.start()
            } catch (e: RuntimeException) {
                Log.i(
                    TAG,
                    "caught RuntimeException during StartTask(): " + e.message
                )
            } finally {
            }
        }

        init {
            service = s
        }
    }
}