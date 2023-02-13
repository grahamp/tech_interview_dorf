package com.example.compose_learning

import androidx.lifecycle.LiveData

interface ISwitchDataSource {
    fun getData(): LiveData<Result<SwitchData>>
    fun connect()
    fun sendData(switchData: SwitchData)

    /*
         Method for for controlling the FAKE not implemented in the real.
        */
    fun disconnect()
}