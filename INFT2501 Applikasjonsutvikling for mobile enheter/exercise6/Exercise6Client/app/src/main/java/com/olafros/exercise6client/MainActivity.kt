package com.olafros.exercise6client

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.olafros.exercise6client.ui.theme.Exercise6ClientTheme
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientViewModel : ViewModel() {

    private val SERVER_IP: String = "10.0.2.2";
    private val SERVER_PORT: Int = 12345;
    var server: Socket? = null
        private set

    var incomingMessages = mutableStateListOf<String>()
        private set

    var outgoingMessages = mutableStateListOf<String>()
        private set

    fun start() {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                incomingMessages.add("Kobler til Tjener ...")

                server = Socket(SERVER_IP, SERVER_PORT)

                incomingMessages.add("Koblet til tjener:\n$server")

                startReadFromServer()

                sendToServer("Heisann Tjener! Hyggelig å hilse på deg")
            } catch (e: IOException) {
                e.printStackTrace()
                e.message?.let { incomingMessages.add(it) }
            }
        }
    }

    private suspend fun startReadFromServer() = coroutineScope {
        CoroutineScope(Dispatchers.IO).launch {
            server?.let {
                while (true) {
                    val reader = BufferedReader(InputStreamReader(it.getInputStream()))
                    val message = reader.readLine()
                    if (message !== null) {
                        incomingMessages.add(message)
                    } else {
                        it.close()
                        break
                    }
                }
            }
        }
    }

    suspend fun sendToServer(message: String) = coroutineScope {
        Log.i("Send", message)
        CoroutineScope(Dispatchers.IO).launch {
            server?.let {
                val writer = PrintWriter(it.getOutputStream(), true)
                writer.println(message)
                outgoingMessages.add(message)
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    private val clientViewModel by viewModels<ClientViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientViewModel.start()
        setContent {
            Exercise6ClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold {
                        Column {
                            if (clientViewModel.server !== null) {
                                SendMessageView(clientViewModel::sendToServer)
                            }
                            Row {
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .fillMaxWidth()
                                ) {
                                    item {
                                        Text("Meldinger sendt:", fontWeight = FontWeight.Bold)
                                    }
                                    items(clientViewModel.outgoingMessages) { message ->
                                        Text(message)
                                        Divider()
                                    }
                                }
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .fillMaxWidth()
                                ) {
                                    item {
                                        Text("Meldinger mottatt:", fontWeight = FontWeight.Bold)
                                    }
                                    items(clientViewModel.incomingMessages) { message ->
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
    }
}

@Composable
fun SendMessageView(sendToServer: suspend (String) -> Unit) {
    val messageState = remember { mutableStateOf(TextFieldValue()) }
    fun send() = runBlocking {
        sendToServer(messageState.value.text)
    }
    Column {
        Text("Send en melding til andre tilkoblede klienter")
        TextField(
            value = messageState.value,
            onValueChange = { messageState.value = it },
            label = { Text("Melding") })
        Button(onClick = { send() }) {
            Text("Send")
        }
    }
}