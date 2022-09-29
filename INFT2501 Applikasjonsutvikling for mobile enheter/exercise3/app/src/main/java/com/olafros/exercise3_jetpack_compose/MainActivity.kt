package com.olafros.exercise3_jetpack_compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.olafros.exercise3_jetpack_compose.ui.theme.Exercise3_Jetpack_ComposeTheme

class FriendsViewModel : ViewModel() {

    var friends = mutableStateListOf<Friend>()
        private set

    fun addItem(name: String, birthDate: String) {
        val id = (friends.map { it.id }.maxOrNull() ?: 0) + 1
        Log.w("Id", id.toString())
        friends.add(Friend(id, name, birthDate))
    }

    fun updateFriend(id: Int, name: String, birthdate: String) {
        friends.filter { it.id == id }.forEach {
            it.name = name
            it.birthDate = birthdate
        }
    }
}

class MainActivity : ComponentActivity() {
    private val friendsViewModel by viewModels<FriendsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Exercise3_Jetpack_ComposeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Main(
                        friendsViewModel.friends,
                        friendsViewModel::addItem,
                        friendsViewModel::updateFriend
                    )
                }
            }
        }
    }
}

@Composable
private fun Main(
    friends: List<Friend>,
    addFriend: (String, String) -> Unit,
    editFriend: (Int, String, String) -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "friendsList") {
        composable(route = "friendsList") {
            FriendsListScreen(navController, friends, addFriend)
        }
        composable(
            route = "friendDetails/{friendId}",
            arguments = listOf(navArgument("friendId") { type = NavType.IntType })
        ) {
            FriendDetailsScreen(
                navController,
                it.arguments!!.getInt("friendId"),
                friends,
                editFriend
            )
        }
    }
}

@Composable
fun FriendsListScreen(
    navController: NavController,
    friends: List<Friend>,
    addFriend: (String, String) -> Unit
) {
    Column {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            item {
                Text(text = "Legg til venn")
                addFriendBox(addFriend)
                Divider()
            }
            items(friends) { friend ->
                FriendListItem(navController, friend)
            }
        }
    }
}

@Composable
fun addFriendBox(
    addFriend: (String, String) -> Unit
) {
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val birthdateState = remember { mutableStateOf(TextFieldValue()) }
    fun add() {
        addFriend(nameState.value.text, birthdateState.value.text)
    }
    Column {
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Navn") })
        TextField(
            value = birthdateState.value,
            onValueChange = { birthdateState.value = it },
            label = { Text("Fødselsdag") }
        )
        Button(onClick = { add() }) {
            Text("Legg til venn")
        }
    }
}

@Composable
fun FriendListItem(navController: NavController, friend: Friend) {
    Column(
        Modifier
            .clickable { navController.navigate("friendDetails/${friend.id}") }
            .padding(5.dp)) {
        Text(text = "Navn: ${friend.name}")
        Text(text = "Fødselsdag: ${friend.birthDate}")
        Divider()
    }
}

@Composable
fun FriendDetailsScreen(
    navController: NavController,
    friendId: Int,
    friends: List<Friend>,
    editFriend: (Int, String, String) -> Unit
) {
    val friend = friends.find { it.id == friendId }
        ?: return Scaffold {
            Button(onClick = {
                navController.navigate("friendsList")
            }) {
                Text("Noe gikk galt, gå tilbake")
            }
        }
    val nameState = remember { mutableStateOf(TextFieldValue(text = friend.name)) }
    val birthdateState = remember { mutableStateOf(TextFieldValue(text = friend.birthDate)) }
    fun saveFriend() {
        editFriend(friendId, nameState.value.text, birthdateState.value.text)
        navController.navigate("friendsList")
    }
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { saveFriend() }) {
                Text("Lagre")
            }
        }
    ) {
        Column(Modifier.padding(8.dp)) {
            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Navn") })
            TextField(
                value = birthdateState.value,
                onValueChange = { birthdateState.value = it },
                label = { Text("Fødselsdag") })
        }
    }
}
