package com.olafros.exercise6host

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import com.olafros.exercise6host.ui.theme.Exercise6HostTheme
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class HostViewModel : ViewModel() {

    val PORT = 12345

    private var connectedSockets = mutableStateListOf<Socket>()

    var outgoingMessages = mutableStateListOf<String>()
        private set

    private var ui: String? = ""
        set(str) {
            if (str != null) {
                MainScope().launch {
                    outgoingMessages.add(str)
                }
            }
            field = str
        }

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                ui = "Starter Tjener ..."
                ServerSocket(PORT).use { serverSocket: ServerSocket ->

                    ui = "ServerSocket opprettet, venter på at en klient kobler seg til...."
                    while (true) {
                        val clientSocket = serverSocket.accept()
                        ui = "En Klient koblet seg til:\n$clientSocket"

                        handleNewClient(clientSocket)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                ui = e.message
            }
        }
    }

    private suspend fun handleNewClient(clientSocket: Socket) =
        coroutineScope { // this: CoroutineScope
            CoroutineScope(Dispatchers.IO).launch {
                connectedSockets.add(clientSocket)
                ui = "En Klient koblet seg til:\n$clientSocket"

                //send tekst til klienten
                sendToClient(clientSocket, "Velkommen Klient!")

                // Hent tekst fra klienten
                readFromClient(clientSocket)
            }
        }

    private suspend fun readFromClient(socket: Socket) = coroutineScope {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val message = reader.readLine()
                if (message == null) {
                    ui = "Klient koblet fra"
                    connectedSockets.remove(socket)
                    socket.close()
                    break
                } else {
                    connectedSockets.filter { it !== socket }.forEach { sendToClient(it, message) }
                    ui = "Klienten sier:\n$message"
                }
            }
        }
    }

    private suspend fun sendToClient(socket: Socket, message: String) = coroutineScope {
        CoroutineScope(Dispatchers.IO).launch {
            val writer = PrintWriter(socket.getOutputStream(), true)
            writer.println(message)
            ui = "Sendte følgende til klienten:\n$message"
        }
    }
}

class MainActivity : ComponentActivity() {
    private val hostViewModel by viewModels<HostViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hostViewModel.start()
        setContent {
            Exercise6HostTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold {
                        LazyColumn {
                            item {
                                Text("Meldinger:", fontWeight = FontWeight.Bold)
                            }
                            items(hostViewModel.outgoingMessages) { message ->
                                Text(message)
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}
