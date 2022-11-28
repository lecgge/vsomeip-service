package com.example.server

import android.util.Log

class VSomeIPService(var appName: String,var listener: IServiceListener) {

    init {
        create(appName,this)
    }

    private var startTask = StartTask(this)

    companion object{
        private val TAG = "SomeIPService"

        init {
            System.loadLibrary("ServiceJNI")
        }
    }


    external private fun create(appName: String?, service: VSomeIPService)
    external private fun init(): Boolean
    external private fun offer_service(serviceId: Int, instanceId: Int)
    external private fun offer_event(serviceId: Int, instanceId: Int, eventId: Int, eventGroupId: Int)
    external private fun offer_field(serviceId: Int, instanceId: Int, fieldId: Int, fieldGroupId: Int)
    external private fun stop_offer_event(serviceId: Int, instanceId: Int, eventId: Int)
    external private fun stop_offer_field(serviceId: Int, instanceId: Int, fieldId: Int)
    external private fun start()
    external private fun stop()
    external private fun notify(serviceId: Int, instanceId: Int, eventId: Int, payload: ByteArray?)
    external private fun send_response(serviceId: Int, instanceId: Int, methodId: Int, payload: ByteArray?)
    external private fun close()

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
    fun notifyEvent(serviceId: Int, instanceId: Int, eventId: Int, payload: ByteArray?) {
        notify(serviceId, instanceId, eventId, payload)
    }

    fun sendResponse(serviceId: Int, instanceId: Int, methodId: Int, payload: ByteArray?) {
        send_response(serviceId, instanceId, methodId, payload)
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