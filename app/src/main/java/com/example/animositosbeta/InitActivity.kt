package com.example.animositosbeta

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class InitActivity : AppCompatActivity() {

    private lateinit var bluetoothService: BluetoothService
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private lateinit var btnGrabar: Button

    // Solicitar permisos en tiempo de ejecución
    private val bluetoothPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.BLUETOOTH_CONNECT] == true &&
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            setupBluetooth()  // Si los permisos son otorgados, configuramos Bluetooth
        } else {
            Log.e("InitActivity", "Permisos de Bluetooth no otorgados")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio)

        val kidName = intent.getStringExtra("kidName") ?: ""
        val btnNotificaciones: Button = findViewById(R.id.btn_notificaciones)
        val btnDirectorio: Button = findViewById(R.id.btn_directorio)
        val btnAudios: Button = findViewById(R.id.btn_registro)
        btnGrabar = findViewById(R.id.btn_grabar)

        bluetoothService = BluetoothService(this)

        // Verificar los permisos de Bluetooth antes de hacer cualquier acción
        checkBluetoothPermissions()

        btnNotificaciones.setOnClickListener {
            // Inicia la actividad de Notificaciones
            val intent = Intent(this, NotificationsActivity::class.java).apply {
                putExtra("kidName", kidName) // Pasar el nombre del niño a NotificationsActivity
            }
            startActivity(intent)
        }

        btnDirectorio.setOnClickListener {
            // Iniciar la actividad de Directorio
            val intent = Intent(this, DirectoryActivity::class.java)
            startActivity(intent)
        }

        btnAudios.setOnClickListener {
            val intent = Intent(this, AudioActivity::class.java)
            startActivity(intent)
        }

        btnGrabar.setOnClickListener {
            if (bluetoothAdapter == null) {
                // Bluetooth no es compatible con este dispositivo
                return@setOnClickListener
            }

            if (!bluetoothAdapter.isEnabled) {
                // Solicitar que el Bluetooth se habilite
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, 1)
            } else {
                // Buscar dispositivos Bluetooth
                discoverDevices()
            }
        }
    }

    private fun checkBluetoothPermissions() {
        val bluetoothConnectPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        // Si no tenemos los permisos, solicitarlos
        if (bluetoothConnectPermission != PackageManager.PERMISSION_GRANTED ||
            locationPermission != PackageManager.PERMISSION_GRANTED) {
            bluetoothPermissionRequest.launch(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION)
            )
        } else {
            setupBluetooth()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupBluetooth() {
        if (bluetoothAdapter == null) {
            // Bluetooth no es compatible con este dispositivo
            Log.e("InitActivity", "Este dispositivo no soporta Bluetooth")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            // Solicitar que el Bluetooth se habilite
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }
    }

    @SuppressLint("MissingPermission")
    private fun discoverDevices() {
        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter?.bondedDevices ?: return
        for (device in pairedDevices) {
            Log.d("InitActivity", "Dispositivo encontrado: ${device.name}, ${device.address}")
            if (device.name == "YourDeviceName") { // Asegúrate de que coincida con el nombre de tu grabadora
                if (bluetoothService.connectToBluetoothDevice(device)) {
                    startRecording()  // Inicia la grabación
                }
            }
        }
    }

    private fun startRecording() {
        // Cambiar color del botón a rojo
        btnGrabar.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
        btnGrabar.text = "Grabando..."

        // Iniciar la grabación en el BluetoothService
        bluetoothService.startRecording()

        // Aquí puedes agregar una forma de detener la grabación, como un temporizador o un botón de detener grabación
    }

    private fun stopRecording() {
        // Cambiar color del botón a su estado original
        btnGrabar.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        btnGrabar.text = "Grabar"

        // Detener la grabación
        bluetoothService.stopRecording()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothService.close()  // Cierra la conexión cuando la actividad se destruya
    }

    // Manejo de la respuesta para habilitar el Bluetooth
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Bluetooth habilitado
                discoverDevices()
            } else {
                // Si el usuario canceló, puedes manejarlo aquí
                Log.e("InitActivity", "Bluetooth no habilitado")
            }
        }
    }
}
