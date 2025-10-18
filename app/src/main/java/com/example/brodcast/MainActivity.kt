package com.example.brodcast

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val phoneNumbers = listOf("1234567890", "0987654321")
    private lateinit var numbersListView: ListView
    private val SMS_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate - Iniciando aplicación")
        
        try {
            setContentView(R.layout.activity_main)
            Log.d(TAG, "onCreate - Layout establecido correctamente")

            // Inicializar ListView
            numbersListView = findViewById(R.id.numbersListView)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, phoneNumbers)
            numbersListView.adapter = adapter
            Log.d(TAG, "onCreate - ListView configurado con ${phoneNumbers.size} números")

            // Verificar y solicitar permisos
            checkAndRequestSmsPermission()
            
        } catch (e: Exception) {
            Log.e(TAG, "onCreate - Error al inicializar la aplicación", e)
            Toast.makeText(this, "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) 
            != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permiso RECEIVE_SMS no otorgado, solicitando...")
            ActivityCompat.requestPermissions(
                this, 
                arrayOf(Manifest.permission.RECEIVE_SMS), 
                SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d(TAG, "Permiso RECEIVE_SMS ya otorgado")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            SMS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso RECEIVE_SMS otorgado por el usuario")
                    Toast.makeText(this, "Permiso de SMS otorgado", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(TAG, "Permiso RECEIVE_SMS denegado por el usuario")
                    Toast.makeText(this, "Permiso de SMS denegado. La app no podrá recibir SMS.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart - Aplicación visible")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume - Aplicación en primer plano")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause - Aplicación pausada")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy - Aplicación destruida")
    }
}
