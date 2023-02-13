package com.example.compose_learning.ble.parsers

import com.welie.blessed.BluetoothBytesParser
import java.util.*

data class ButtonStateParser(val buttonStateNumber : Int)
    /* Let the presentation layer map the raw info to language "pressed" or whatever,
    also preserve the raw info with all details through the "data" stream.
    Data can get rounded and converted to different units multiple times otherwise resulting
    in confusion and data loss.
     */
 {
    companion object {
        fun fromBytes(value: ByteArray): ButtonStateParser {
            val parser = BluetoothBytesParser(value)
            val buttonStateNumber = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
            return ButtonStateParser(
                buttonStateNumber = buttonStateNumber
            )
        }
    }
}