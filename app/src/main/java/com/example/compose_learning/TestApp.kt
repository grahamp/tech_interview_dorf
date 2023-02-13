package com.example.compose_learning

import android.app.Application
import com.example.compose_learning.ble.IBluetoothHandler

class TestApp : Application() {

    companion object {
        lateinit var instance: TestApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private var switchDataSource: ISwitchDataSource? = null

    fun provideSwitchData(bluetoothHandler: IBluetoothHandler): ISwitchDataSource {
        if (null == switchDataSource) {
            switchDataSource = BLESwitchDataSource(bluetoothHandler)
        }
        return switchDataSource!!
    }

}