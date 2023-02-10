package com.example.compose_learning

import android.app.Application

class TestApp : Application() {

    companion object {
        lateinit var instance: TestApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        switchDataSource =  FakeSwitchDataSource()
    }
    lateinit var switchDataSource : FakeSwitchDataSource



}