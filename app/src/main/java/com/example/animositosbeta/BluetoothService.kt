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
import kotlin.concurrent.thread

class BluetoothService(private val context: Context) {

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private val uuid = UUID.fromString("0000111e-0000-1000-8000-00805f9b34fb")  // UUID estándar para SPP

    private var isRecording = false  // Estado de grabación

    // Conectar al dispositivo Bluetooth con nombre específico
    @SuppressLint("MissingPermission")
    fun connectToBluetoothDeviceByName(deviceName: String): Boolean {
        var isConnected = false
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        pairedDevices?.forEach { device ->
            if (device.name == deviceName) {  // Verificamos si el nombre coincide
                Log.d("BluetoothService", "Dispositivo encontrado: ${device.name}")
                isConnected = connectToBluetoothDevice(device)  // Conectamos al dispositivo
                return@forEach
            }
        }

        if (!isConnected) {
            Log.e("BluetoothService", "No se encontró el dispositivo con nombre: $deviceName")
        }

        return isConnected
    }

    // Conectar al dispositivo Bluetooth
    @SuppressLint("MissingPermission")
    fun connectToBluetoothDevice(device: BluetoothDevice): Boolean {
        if (bluetoothSocket?.isConnected == true) {
            Log.d("BluetoothService", "Ya está conectado al dispositivo: ${device.name}")
            return true  // Si ya está conectado, no hacer nada
        }

        var isConnected = false
        var attempt = 0
        val maxAttempts = 3  // Número máximo de intentos para conectar

        // Lista de UUIDs disponibles en el dispositivo
        listDeviceUUIDs(device)

        while (!isConnected && attempt < maxAttempts) {
            try {
                Log.d("BluetoothService", "Intentando conectar al dispositivo ${device.name} con la dirección ${device.address}")
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream
                inputStream = bluetoothSocket?.inputStream
                isConnected = true
                Log.d("BluetoothService", "Conexión exitosa con el dispositivo: ${device.name}")
            } catch (e: IOException) {
                attempt++
                Log.e("BluetoothService", "Error al conectar con el dispositivo. Intento #$attempt", e)
                if (attempt >= maxAttempts) {
                    Log.e("BluetoothService", "No se pudo conectar después de $maxAttempts intentos.")
                    return false
                }
                // Esperar un poco antes de intentar nuevamente
                try {
                    Thread.sleep(1000) // Esperar 1 segundo antes de reintentar
                } catch (interrupted: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
        return isConnected
    }

    // Verificar y mostrar los UUIDs del dispositivo
    private fun listDeviceUUIDs(device: BluetoothDevice) {
        try {
            val uuidSet: Set<UUID> = device.uuids?.map { it.uuid }?.toSet() ?: emptySet()
            if (uuidSet.isNotEmpty()) {
                Log.d("BluetoothService", "UUIDs disponibles en el dispositivo ${device.name}: $uuidSet")
            } else {
                Log.e("BluetoothService", "El dispositivo no tiene UUIDs disponibles")
            }
        } catch (e: Exception) {
            Log.e("BluetoothService", "Error al obtener UUIDs del dispositivo ${device.name}: ${e.message}", e)
        }
    }

    // Iniciar la grabación
    fun startRecording() {
        if (isRecording) {
            Log.d("BluetoothService", "Ya se está grabando.")
            return  // Si ya está grabando, no hacer nada
        }

        thread {  // Ejecutamos en un hilo secundario para no bloquear la UI
            try {
                // Verificar si el socket está conectado y si outputStream no es null
                if (bluetoothSocket?.isConnected == true && outputStream != null) {
                    outputStream?.write("START_RECORDING".toByteArray()) // Comando para iniciar la grabación
                    Log.d("BluetoothService", "Comando de grabación enviado")
                    isRecording = true  // Actualizamos el estado de grabación en el hilo principal
                } else {
                    Log.e("BluetoothService", "Error: Socket desconectado o outputStream no disponible al intentar iniciar grabación")
                }
            } catch (e: IOException) {
                Log.e("BluetoothService", "Error al enviar comando de grabación", e)
            }
        }
    }

    // Detener la grabación
    fun stopRecording() {
        if (!isRecording) {
            Log.d("BluetoothService", "No hay grabación en curso.")
            return  // Si no está grabando, no hacer nada
        }

        thread {  // Ejecutamos en un hilo secundario para no bloquear la UI
            try {
                // Verificamos si el socket sigue conectado antes de intentar escribir
                if (bluetoothSocket?.isConnected == true && outputStream != null) {
                    outputStream?.write("STOP_RECORDING".toByteArray()) // Comando para detener la grabación
                    Log.d("BluetoothService", "Comando de detención enviado")
                    isRecording = false  // Actualizamos el estado de grabación en el hilo principal
                } else {
                    Log.e("BluetoothService", "Error: Socket desconectado o outputStream no disponible al intentar detener grabación")
                }
            } catch (e: IOException) {
                Log.e("BluetoothService", "Error al enviar comando de detención", e)
            }
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

    // Método para alternar grabación y detención de grabación
    fun toggleRecording() {
        if (isRecording) {
            stopRecording()  // Si está grabando, lo detenemos
        } else {
            startRecording()  // Si no está grabando, lo iniciamos
        }
    }

    // Método para leer la respuesta del dispositivo
    private fun readResponse() {
        thread {
            try {
                val buffer = ByteArray(1024)
                var bytes: Int
                while (true) {
                    if (inputStream != null) {
                        bytes = inputStream!!.read(buffer)
                        val response = String(buffer, 0, bytes)
                        Log.d("BluetoothService", "Respuesta recibida del dispositivo: $response")
                        // Aquí puedes verificar la respuesta recibida y actuar en consecuencia
                    }
                }
            } catch (e: IOException) {
                Log.e("BluetoothService", "Error al leer la respuesta", e)
            }
        }
    }
}
