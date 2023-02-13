package com.example.compose_learning.ble

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.lifecycle.LiveData
import com.example.compose_learning.BLESwitchDataSource
import com.example.compose_learning.SwitchData
import com.example.compose_learning.TestApp
import com.example.compose_learning.ble.parsers.ButtonStateParser
import com.welie.blessed.*
import kotlinx.coroutines.*
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.*

interface IBluetoothHandler {
    fun connect()
}

class BluetoothHandler private constructor(context: Context) : IBluetoothHandler {
    /* Replace value with your SensorTag to run for yourself.
       This is hard coded because my TI SensorTag is so old there is no
       firmware update available. And scanning seems to fail.
       Scanning and selecting a device also seems unneeded to meet the 
       goals of this exercise which for me is about the need to plug in
       robust fakes and mock when developing and testing Android IOT application.
     */
    val GRAHAM_POORS_SENSOR_TAG_ADDRESS ="BC:6A:29:AB:E0:3A"
    val peripheralAddress: String = GRAHAM_POORS_SENSOR_TAG_ADDRESS

    fun handlePeripheral(peripheral: BluetoothPeripheral) {
        scope.launch {
            try {
                val mtu = peripheral.requestMtu(185)
                Timber.i("MTU is $mtu")

                peripheral.requestConnectionPriority(ConnectionPriority.HIGH)

                val rssi = peripheral.readRemoteRssi()
                Timber.i("RSSI is $rssi")

                peripheral.getCharacteristic(
                    DIS_SERVICE_UUID,
                    MANUFACTURER_NAME_CHARACTERISTIC_UUID
                )?.let {
                    val manufacturerName = peripheral.readCharacteristic(it)
                    Timber.i("Received: $manufacturerName")
                }

                val model = peripheral.readCharacteristic(
                    DIS_SERVICE_UUID,
                    MODEL_NUMBER_CHARACTERISTIC_UUID
                )
                Timber.i("Received: $model")

                setupButtonNotifications(peripheral)

            } catch (e: IllegalArgumentException) {
                Timber.e(e)
            } catch (b: java.lang.Exception) {
                Timber.e(b)
            }
        }
    }

    private suspend fun setupButtonNotifications(peripheral: BluetoothPeripheral) {
        peripheral.getCharacteristic(KEY_SERVICE_UUID, KEY_PRESS_STATE_UUID)
            ?.let { buttonStateCharacteristic ->
                peripheral.observe(buttonStateCharacteristic) { value ->
                    val buttonState = ButtonStateParser.fromBytes(value)
                    val buttonString = if (buttonState.buttonStateNumber > 0 ) "Button "+buttonState.buttonStateNumber+ "PRESSED" else "Released"
                    val switchData : SwitchData = SwitchData(buttonString,System.currentTimeMillis())
                    TestApp.instance.provideSwitchData(getInstance(TestApp.instance)).sendData(switchData)
                    Timber.i("Received Button Press: %s", buttonState)

                }
            }
    }


    private fun startScanning() {
        central.scanForPeripheralsWithServices(supportedServices,
            { peripheral, scanResult ->
                Timber.i("Found peripheral '${peripheral.name}' with RSSI ${scanResult.rssi}")
                central.stopScan()
                connectPeripheral(peripheral)
            },
            { scanFailure -> Timber.e("scan failed with reason $scanFailure") })
    }

    private fun connectPeripheral(peripheral: BluetoothPeripheral) {
        peripheral.observeBondState {
            Timber.i("Bond state is $it")
        }

        scope.launch {
            try {
                central.connectPeripheral(peripheral)
            } catch (connectionFailed: ConnectionFailedException) {
                Timber.e("connection failed")
            }
        }
    }

    override fun connect() {
        val peripheral = central.getPeripheral(peripheralAddress)

        // Check if this peripheral should still be auto connected
        if (peripheral.getState() == ConnectionState.DISCONNECTED) {
            central.autoConnectPeripheral(peripheral)
        } else {
            handlePeripheral(peripheral)
        }
    }

    companion object {
        // UUIDs for the Device Information service (DIS)
        private val DIS_SERVICE_UUID: UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
        private val MANUFACTURER_NAME_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb")
        private val MODEL_NUMBER_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb")

        // UUIDs for the Battery Service (BAS)
        private val BTS_SERVICE_UUID: UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")
        private val BATTERY_LEVEL_CHARACTERISTIC_UUID: UUID =
            UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb")

        private var instance: BluetoothHandler? = null
        private val sensorTagUuid = UUID.fromString("0000aa80-0000-1000-8000-00805f9b34fb")
        private val KEY_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        private val KEY_PRESS_STATE_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
        private val clientCharacteristicConfigUuid =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private val supportedServices = arrayOf(clientCharacteristicConfigUuid, sensorTagUuid)

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): BluetoothHandler {
            if (instance == null) {
                instance = BluetoothHandler(context.applicationContext)
            }
            return requireNotNull(instance)
        }
    }

    @JvmField
    var central: BluetoothCentralManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        Timber.plant(DebugTree())
        central = BluetoothCentralManager(context)

        central.observeConnectionState { peripheral, state ->
            Timber.i("Peripheral '${peripheral.name}' is $state")
            when (state) {
                ConnectionState.CONNECTED -> handlePeripheral(peripheral)
                ConnectionState.DISCONNECTED -> scope.launch {
                    delay(15000)

                    // Check if this peripheral should still be auto connected
                    if (central.getPeripheral(peripheral.address)
                            .getState() == ConnectionState.DISCONNECTED
                    ) {
                        central.autoConnectPeripheral(peripheral)
                    }
                }
                else -> {
                }
            }
        }

        central.observeAdapterState { state ->
            when (state) {
                BluetoothAdapter.STATE_ON -> startScanning()
            }
        }

        startScanning()
    }
}