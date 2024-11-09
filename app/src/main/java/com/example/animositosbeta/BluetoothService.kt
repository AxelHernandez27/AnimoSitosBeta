package com.example.animositosbeta

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService(private val context: Context) {

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private val uuid = UUID.fromString("0000110A-0000-1000-8000-00805F9B34FB")  // A2DP

    // Conectar al dispositivo Bluetooth
    @SuppressLint("MissingPermission")
    fun connectToBluetoothDevice(device: BluetoothDevice): Boolean {
        return try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            inputStream = bluetoothSocket?.inputStream
            Log.d("BluetoothService", "Conexión exitosa con el dispositivo")
            true
        } catch (e: IOException) {
            Log.e("BluetoothService", "Error al conectar con el dispositivo", e)
            close()  // Cerrar el socket en caso de error
            false
        }
    }

    // Iniciar la grabación
    fun startRecording() {
        try {
            outputStream?.write("START_RECORDING".toByteArray()) // Comando ejemplo
            Log.d("BluetoothService", "Comando de grabación enviado")
        } catch (e: IOException) {
            Log.e("BluetoothService", "Error al enviar comando de grabación", e)
        }
    }

    // Detener la grabación
    fun stopRecording() {
        try {
            outputStream?.write("STOP_RECORDING".toByteArray()) // Comando ejemplo
            Log.d("BluetoothService", "Comando de detención enviado")
        } catch (e: IOException) {
            Log.e("BluetoothService", "Error al enviar comando de detención", e)
        }
    }

    // Cerrar conexión
    fun close() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            Log.d("BluetoothService", "Conexión cerrada correctamente")
        } catch (e: IOException) {
            Log.e("BluetoothService", "Error al cerrar conexión", e)
        }
    }
}
