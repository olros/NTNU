import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class ThreadSocketServer(con: Socket) : Thread() {
    private val connection = con;
    override fun run() {
        val readConnection = InputStreamReader(connection.getInputStream())
        readConnection.use {
            val reader = BufferedReader(readConnection)
            reader.use {
                val writer = PrintWriter(connection.getOutputStream(), true)
                writer.use {
                    writer.println("Hi, you have connected to the server")
                    writer.println("Write an equation and I'll calculate it for you, separate by space (e.g: 5 + 5). Exit with enter-click")

                    //  Receives data from client
                    var input = reader.readLine()
                    while (input != null) {
                        println("A client wrote: $input")
                        writer.println("$input = ${calc(input)}")
                        input = reader.readLine()
                    }
                    connection.close()
                }
            }
        }
    }

    companion object {
        fun calc(equation: String): Int {
            val parts = equation.split(" ")
            val firstNumber = parts[0].toInt()
            val symbol = parts[1]
            val secondNumber = parts[2].toInt()
            return when (symbol) {
                "+" -> firstNumber + secondNumber
                "-" -> firstNumber - secondNumber
                "*" -> if (secondNumber == 0) 0 else firstNumber * secondNumber
                "/" -> if (secondNumber == 0) 0 else firstNumber / secondNumber
                else -> 0
            }
        }
    }
}

fun main() {
    val PORT_NUMBER = 1250
    val server = ServerSocket(PORT_NUMBER)
    println("Log for server. Now we wait...")
    for (i in 0 until 5) {
        try {
            val connection = server.accept()
            println("Thread nr: ${i + 1} waiting for client")
            val t = ThreadSocketServer(connection)
            t.start()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}