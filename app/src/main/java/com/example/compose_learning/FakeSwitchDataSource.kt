package com.example.compose_learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class FakeSwitchDataSource : ISwitchDataSource {
    private val data = MutableLiveData<Result<SwitchData>>()
    private var count = 0
    private var connected: Boolean = false

    constructor() {
        connect() //
    }

    override fun getData(): LiveData<Result<SwitchData>> {
        return data
    }

    override fun connect() {
        connected = true
        start()
    }

    /*
    Method for for controlling the FAKE not implemented in the real.
   */
    public fun forceFakeError() {
        data.postValue(Result.failure(Error("Fake Switch Error")))
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
            while (connected) {
                delay(3000)
                count += 1
                data.postValue(Result.success(SwitchData("Item $count")))
            }
        }

        // Completes the thing
        data.postValue(null)
    }
}
