package com.example.compose_learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//
//enum class ConnectionCommand(val value: Int) {
//    CONNECT(1),
//    DISCONNECT(2),
//    STOP(3);
//}

// Model
//data class ConnectionRequest(
//    val command: ConnectionCommand = ConnectionCommand.CONNECT,
//    val id: Long = System.nanoTime()
//)

class SwitchDataVM : ViewModel() {

    private val switchData = MutableLiveData<ISwitchDataSource>()
    fun connect()
    {
        TestApp.instance.switchDataSource.start()
    }
    fun getSwitchData(): LiveData<Result<SwitchData>> = TestApp.instance.switchDataSource.getData()

}