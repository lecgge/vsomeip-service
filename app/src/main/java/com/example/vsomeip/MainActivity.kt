package com.example.vsomeip

import android.os.Bundle
import android.system.ErrnoException
import android.system.Os
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.vsomeip.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("vsomeip-test", "server-Address: ${getIPAddress(true)}")
        init_vsomeip()

        vSomeIPService = VSomeIPService("Server", DemoServiceListener())
        if (!vSomeIPService.initialize()) {
            Log.d(TAG, "init someip service failed!")
        } else {
            //NavigationTrailInfo
            vSomeIPService.offerService(
                DemoConfig.NavigationTrailInfo.ServiceID,
                DemoConfig.NavigationTrailInfo.InstanceID
            )
            vSomeIPService.offerEvent(
                DemoConfig.NavigationTrailInfo.ServiceID,
                DemoConfig.NavigationTrailInfo.InstanceID,
                DemoConfig.NavigationTrailInfo.EventID,
                DemoConfig.NavigationTrailInfo.EventGroupID,
            )
            //NavigationWeaInfo
            vSomeIPService.offerService(
                DemoConfig.NavigationWeaInfo.ServiceID,
                DemoConfig.NavigationWeaInfo.InstanceID
            )
            vSomeIPService.offerEvent(
                DemoConfig.NavigationWeaInfo.ServiceID,
                DemoConfig.NavigationWeaInfo.InstanceID,
                DemoConfig.NavigationWeaInfo.EventID1,
                DemoConfig.NavigationWeaInfo.EventGroupID
            )
            vSomeIPService.offerEvent(
                DemoConfig.NavigationWeaInfo.ServiceID,
                DemoConfig.NavigationWeaInfo.InstanceID,
                DemoConfig.NavigationWeaInfo.EventID2,
                DemoConfig.NavigationWeaInfo.EventGroupID
            )

            //MAPDynInfo
            vSomeIPService.offerService(
                DemoConfig.MAPDynInfo.ServiceID,
                DemoConfig.MAPDynInfo.InstanceID
            )
            vSomeIPService.offerEvent(
                DemoConfig.MAPDynInfo.ServiceID,
                DemoConfig.MAPDynInfo.InstanceID,
                DemoConfig.MAPDynInfo.EventID1,
                DemoConfig.MAPDynInfo.EventGroupID
            )
            vSomeIPService.offerEvent(
                DemoConfig.MAPDynInfo.ServiceID,
                DemoConfig.MAPDynInfo.InstanceID,
                DemoConfig.MAPDynInfo.EventID2,
                DemoConfig.MAPDynInfo.EventGroupID
            )
            vSomeIPService.offerEvent(
                DemoConfig.MAPDynInfo.ServiceID,
                DemoConfig.MAPDynInfo.InstanceID,
                DemoConfig.MAPDynInfo.EventID3,
                DemoConfig.MAPDynInfo.EventGroupID
            )
            vSomeIPService.offerEvent(
                DemoConfig.MAPDynInfo.ServiceID,
                DemoConfig.MAPDynInfo.InstanceID,
                DemoConfig.MAPDynInfo.EventID4,
                DemoConfig.MAPDynInfo.EventGroupID
            )

            //EgoPosInfo
            vSomeIPService.offerService(
                DemoConfig.EgoPosInfo.ServiceID,
                DemoConfig.EgoPosInfo.InstanceID
            )
            vSomeIPService.offerEvent(
                DemoConfig.EgoPosInfo.ServiceID,
                DemoConfig.EgoPosInfo.InstanceID,
                DemoConfig.EgoPosInfo.EventID,
                DemoConfig.EgoPosInfo.EventGroupID,
            )

            Thread(object : Runnable {
                override fun run() {
                    vSomeIPService.startService()
                }
            }).start()
            Log.d(TAG, "SomeIP Service start.")

            binding.sampleText.setOnClickListener {
//                vSomeIPService.sendResponse(
//                    DemoConfig.ServiceIDs.SAMPLE_SERVICE,
//                    DemoConfig.InstanceIDs.SAMPLE_INSTANCE,
//                    0x1002,
//                    byteArrayOf(0x10, 0x20, 0x30,0x0f),
//                    vSomeIPService.mNativePtr
//                )
                //NavigationWeaInfo
                vSomeIPService.notifyEvent(
                    DemoConfig.NavigationWeaInfo.ServiceID,
                    DemoConfig.NavigationWeaInfo.InstanceID,
                    DemoConfig.NavigationWeaInfo.EventID1,
                    byteArrayOf(0x11, 0x21, 0x31),
                    DemoConfig.ClientIDS.CLIENTID
                )
                vSomeIPService.notifyEvent(
                    DemoConfig.NavigationWeaInfo.ServiceID,
                    DemoConfig.NavigationWeaInfo.InstanceID,
                    DemoConfig.NavigationWeaInfo.EventID2,
                    byteArrayOf(0x10, 0x20, 0x30),
                    DemoConfig.ClientIDS.CLIENTID
                )
                //MAPDynInfo
                vSomeIPService.notifyEvent(
                    DemoConfig.MAPDynInfo.ServiceID,
                    DemoConfig.MAPDynInfo.InstanceID,
                    DemoConfig.MAPDynInfo.EventID1,
                    byteArrayOf(0x33, 0x33, 0x33),
                    DemoConfig.ClientIDS.CLIENTID
                )
                vSomeIPService.notifyEvent(
                    DemoConfig.MAPDynInfo.ServiceID,
                    DemoConfig.MAPDynInfo.InstanceID,
                    DemoConfig.MAPDynInfo.EventID2,
                    byteArrayOf(0x34, 0x34, 0x34),
                    DemoConfig.ClientIDS.CLIENTID
                )
                vSomeIPService.notifyEvent(
                    DemoConfig.MAPDynInfo.ServiceID,
                    DemoConfig.MAPDynInfo.InstanceID,
                    DemoConfig.MAPDynInfo.EventID3,
                    byteArrayOf(0x35, 0x35, 0x35),
                    DemoConfig.ClientIDS.CLIENTID
                )
                vSomeIPService.notifyEvent(
                    DemoConfig.MAPDynInfo.ServiceID,
                    DemoConfig.MAPDynInfo.InstanceID,
                    DemoConfig.MAPDynInfo.EventID4,
                    byteArrayOf(0x36, 0x36, 0x36),
                    DemoConfig.ClientIDS.CLIENTID
                )
                //EgoPosInfo
                vSomeIPService.notifyEvent(
                    DemoConfig.EgoPosInfo.ServiceID,
                    DemoConfig.EgoPosInfo.InstanceID,
                    DemoConfig.EgoPosInfo.EventID,
                    byteArrayOf(0x7f, 0x7f, 0x7f),
                    DemoConfig.ClientIDS.CLIENTID
                )
            }
        }
    }


    companion object {
        const val TAG = "DemoService"

        const val JSONFILENAME: String = "vsomeip-server.json"
        lateinit var vSomeIPService : VSomeIPService
    }

    private fun init_eventAndeventgroup(vSomeIPService : VSomeIPService){

    }

    private fun init_vsomeip() {
        val someipConfig = File(cacheDir, JSONFILENAME)
        try {
            if (someipConfig.exists()) {
                someipConfig.delete()
            }
            someipConfig.createNewFile()
            writeConfigToFile(someipConfig)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            Os.setenv(
                "VSOMEIP_CONFIGURATION",
                applicationContext.cacheDir.path + "/" + JSONFILENAME,
                true
            )
            Os.setenv("VSOMEIP_BASE_PATH", applicationContext.cacheDir.path + "/", true)
            Os.setenv("VSOMEIP_APPLICATION_NAME", "Server", true)
        } catch (e: ErrnoException) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun writeConfigToFile(file: File) {
        val fileOutputStream = FileOutputStream(file)
        var value = getJson(JSONFILENAME)
        val jsonObject = JSONObject(value)
        jsonObject.put("unicast", getIPAddress(true))
        value = jsonObject.toString()
        Log.d("vsomeip-test", "writeConfigToFile: ${value}")
        val bytes = value.toByteArray()
        fileOutputStream.write(bytes)
        fileOutputStream.close()
    }

    fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                    0,
                                    delim
                                ).uppercase(
                                    Locale.getDefault()
                                )
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        } // for now eat exceptions
        return ""
    }

    private fun getJson(fileName: String): String {
        val stringBuffer = StringBuffer()
        val assetManager = assets
        try {
            val reader = BufferedReader(InputStreamReader(assetManager.open(fileName)))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
        } catch (e: Exception) {
        }
        return stringBuffer.toString()
    }
}