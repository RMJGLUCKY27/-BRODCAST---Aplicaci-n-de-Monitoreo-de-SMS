package com.example.brodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    private val TAG = "SmsReceiver"
    private val phoneNumbers = listOf("1234567890", "0987654321") // Números permitidos

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive - Broadcast recibido: ${intent.action}")
        
        try {
            if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                Log.d(TAG, "onReceive - SMS_RECEIVED_ACTION detectado")
                
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                Log.d(TAG, "onReceive - Número de mensajes: ${messages?.size ?: 0}")
                
                if (messages != null) {
                    for (smsMessage in messages) {
                        val sender = smsMessage.originatingAddress
                        val messageBody = smsMessage.messageBody
                        
                        Log.d(TAG, "onReceive - SMS de: $sender")
                        Log.d(TAG, "onReceive - Mensaje: $messageBody")
                        
                        if (phoneNumbers.contains(sender)) {
                            Log.d(TAG, "onReceive - Número autorizado, mostrando notificación")
                            Toast.makeText(
                                context, 
                                "SMS de $sender: $messageBody", 
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Log.d(TAG, "onReceive - Número no autorizado: $sender")
                        }
                    }
                } else {
                    Log.w(TAG, "onReceive - No se pudieron extraer mensajes del intent")
                }
            } else {
                Log.d(TAG, "onReceive - Acción no relacionada con SMS: ${intent.action}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "onReceive - Error al procesar SMS", e)
            Toast.makeText(context, "Error al procesar SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
