package com.olafros.exercise5

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.olafros.exercise5.ui.theme.Exercise5Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

enum class AppView {
    START,
    GAME,
}

enum class GameState {
    BEFORE_FIRST_GUESS,
    BEFORE_SECOND_GUESS,
    BEFORE_THIRD_GUESS,
    CORRECT_GUESS,
    NO_MORE_GUESSES,
}

class GameViewModel : ViewModel() {

    // Commented out as the server returns 502 Bad Gateway,
    // run the local Express version instead `node tallspill.js`
    // private val BASE_URL = "https://bigdata.idi.ntnu.no/mobil/tallspill.jsp"
    private val BASE_URL = "http://localhost:3000/mobil/tallspill.jsp"
    private val network: HttpWrapper = HttpWrapper(BASE_URL)

    var viewOpen by mutableStateOf(AppView.START)
    var gameState by mutableStateOf(GameState.BEFORE_FIRST_GUESS)
        private set

    var feedback: String? = null
        private set

    private fun showFeedback(message: String) {
        Log.i("Snackbar", "Opening snackbar with message: $message")
        feedback = "Tilbakemelding:\n$message"
    }

    fun openView(view: AppView) {
        if (view == AppView.GAME) {
            gameState = GameState.BEFORE_FIRST_GUESS
        }
        viewOpen = view
        feedback = null
    }

    fun startGame(name: String, cardNumber: String) {
        Log.i("StartGame", "Starting game by sending request")
        performRequest(network, mapOf("navn" to name, "kortnummer" to cardNumber)) {
            Log.i("StartGame", "Received response to startGame request: $it")
            val startSuccessMsg = "Oppgi et"
            if (it.contains(startSuccessMsg, ignoreCase = true)) {
                Log.i("StartGame", "StartGame request succeeded")
                openView(AppView.GAME)
            }
            showFeedback(it)
        }
    }

    fun guessNumber(number: String) {
        Log.i("GuessNumber", "Guessing number by sending request")
        performRequest(network, mapOf("tall" to number)) {
            Log.i("GuessNumber", "Received response to GuessNumber request: $it")
            val wonSuccessMsg = "du har vunnet 100 kr som kommer inn på ditt kort"
            showFeedback(it)
            if (it.contains(wonSuccessMsg, ignoreCase = true)) {
                gameState = GameState.CORRECT_GUESS
                Log.i("GuessNumber", "Correct guess")
            } else {
                when (gameState) {
                    GameState.BEFORE_FIRST_GUESS -> gameState = GameState.BEFORE_SECOND_GUESS
                    GameState.BEFORE_SECOND_GUESS -> gameState = GameState.BEFORE_THIRD_GUESS
                    GameState.BEFORE_THIRD_GUESS -> gameState = GameState.NO_MORE_GUESSES
                    else -> Log.e("Gamestate", "Gamestate is in an unknown state")
                }
            }
        }
    }
}

private fun performRequest(
    network: HttpWrapper,
    parameterList: Map<String, String>,
    onResult: (String) -> Unit
) {
    CoroutineScope(IO).launch {
        val response: String = try {
            network.get(parameterList)
        } catch (e: Exception) {
            Log.e("performRequest()", e.message!!)
            e.printStackTrace()
            e.toString()
        }
        MainScope().launch {
            Log.e("Response", response)
            onResult(response)
        }
    }
}

class MainActivity : ComponentActivity() {
    private val gameViewModel by viewModels<GameViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Exercise5Theme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold {
                        Column {
                            if (gameViewModel.viewOpen == AppView.START) {
                                StartView(gameViewModel::startGame)
                            } else {
                                GameView(
                                    gameViewModel::guessNumber,
                                    gameViewModel.gameState,
                                    gameViewModel::openView
                                )
                            }
                            if (gameViewModel.feedback != null) {
                                Log.i("hmm", "---${gameViewModel.feedback}---")
                                Text(gameViewModel.feedback!!)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartView(
    startGame: (String, String) -> Unit,
) {
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val cardNumberState = remember { mutableStateOf(TextFieldValue()) }

    fun start() {
        startGame(nameState.value.text, cardNumberState.value.text)
    }
    Column {
        Text("Fyll ut ditt navn og kortnummer for å starte spillet")
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Navn") })
        TextField(
            value = cardNumberState.value,
            onValueChange = { cardNumberState.value = it },
            label = { Text("Kortnummer") }
        )
        Button(onClick = { start() }) {
            Text("Start spill")
        }
    }
}

@Composable
fun GameView(
    guessNumber: (String) -> Unit,
    gameState: GameState,
    setViewOpen: (AppView) -> Unit,
) {
    val guessState = remember { mutableStateOf(TextFieldValue()) }
    fun guess() {
        guessNumber(guessState.value.text)
    }
    Column {
        when (gameState) {
            GameState.BEFORE_FIRST_GUESS, GameState.BEFORE_SECOND_GUESS, GameState.BEFORE_THIRD_GUESS -> {
                Text("Skriv inn det tallet du tror er riktig")
                TextField(
                    value = guessState.value,
                    onValueChange = { guessState.value = it },
                    label = { Text("Gjett tall") })
                Button(onClick = { guess() }) {
                    Text("Gjett tall")
                }
            }
            GameState.NO_MORE_GUESSES -> {
                Text("Du har brukt opp dine forsøk på å gjette riktig, start et nytt spill for å prøve på nytt")
                Button(onClick = { setViewOpen(AppView.START) }) {
                    Text("Start nytt spill")
                }
            }
            GameState.CORRECT_GUESS -> {
                Text("Du har gjettet riktig! Gratulerer!")
                Button(onClick = { setViewOpen(AppView.START) }) {
                    Text("Start nytt spill")
                }
            }
        }
    }
}
