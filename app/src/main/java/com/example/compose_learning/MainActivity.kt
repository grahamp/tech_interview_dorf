package com.example.compose_learning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import com.example.compose_learning.ui.theme.Compose_learningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Compose_learningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    IOT_Button_State("Initialized")
                }
            }
        }
        val switchDataInstance = SwitchDataVM().getSwitchData()
        switchDataInstance.observeForever {  result ->
            result?.onSuccess { switchDataResult ->
                setContent {
                    Compose_learningTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            IOT_Button_State( "value = ${switchDataResult?.title ?: "Loading.."}")
                        }
                    }
                }
            }
        }

    }
}
@Composable
fun IOT_Button_State(switchStateText: String,  modifier: Modifier = Modifier) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "IOT Button State $switchStateText!")
        Button(onClick = { TestApp.instance.switchDataSource.connect() }) {
            Text(text = "Connect/Disconnect")
        }
    }
}



@Composable
fun DefaultPreview() {
    Compose_learningTheme {
        IOT_Button_State("Connect")
    }

}