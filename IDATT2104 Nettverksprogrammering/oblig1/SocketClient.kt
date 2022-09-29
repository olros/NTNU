import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

fun main() {
    val PORT_NUMBER = 1250
    val readFromCommandLine = Scanner(System.`in`)
    print("Type host of server: ")
    val clientMachine = readFromCommandLine.nextLine()

    // Setup connection to server
    val connection = Socket(clientMachine, PORT_NUMBER)
    connection.use {
        val readConnection = InputStreamReader(connection.getInputStream())
        readConnection.use {
            val reader = BufferedReader(readConnection)
            reader.use {
                val writer = PrintWriter(connection.getOutputStream(), true)
                writer.use {
                    println("Connection was created")
                    // Reads info from server and prints it
                    val introMessage1 = reader.readLine()
                    val introMessage2 = reader.readLine()
                    println("$introMessage1\n$introMessage2")

                    // Reads input from commandline (the user)
                    var oneLine = readFromCommandLine.nextLine()
                    while (oneLine != "") {
                        writer.println(oneLine)
                        val response = reader.readLine()
                        println("From server: $response")
                        oneLine = readFromCommandLine.nextLine()
                    }
                }
            }
        }
    }
}
