package com.example.compose_learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.compose_learning.ble.BluetoothHandler
import com.example.compose_learning.ble.IBluetoothHandler
import com.example.compose_learning.ble.parsers.ButtonStateParser
import kotlinx.coroutines.*


class BLESwitchDataSource : ISwitchDataSource {
    private var bluetoothHandler: IBluetoothHandler
    private val data = MutableLiveData<Result<SwitchData>>()
    private var count = 0
    private var connected: Boolean = false


    constructor(bluetoothHandlerIn: IBluetoothHandler) {
        bluetoothHandler = bluetoothHandlerIn
    }

    override fun getData(): LiveData<Result<SwitchData>> {
        return data
    }

    override fun connect() {
        bluetoothHandler.connect()
        start()
    }

    override fun sendData(switchData: SwitchData) {
        data.postValue(Result.success(switchData))
    }

    /*
     Method for for controlling the FAKE not implemented in the real.
    */
    override fun disconnect() {
        connected = false
    }

    fun start() = runBlocking<Unit> {
        // Fetch the data from a remote source or database, and then set the value of the data using setValue or postValue.
        // Declare a coroutine
        GlobalScope.launch {

            data.postValue(Result.success(SwitchData("Item $count")))

        }

        // Completes the thing
        data.postValue(null)
    }

    fun send(buttonState: Int)= runBlocking<Unit> {

        GlobalScope.launch {

            data.postValue(Result.success(SwitchData("Item $buttonState")))

        }
    }
}
