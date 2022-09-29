import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class ThreadWebServer(con: Socket) : Thread() {
    private val connection = con;
    override fun run() {
        val readConnection = InputStreamReader(connection.getInputStream())
        readConnection.use {
            val reader = BufferedReader(readConnection)
            reader.use {
                val writer = PrintWriter(connection.getOutputStream(), true)
                writer.use {
                    writer.println("HTTP/1.0 200 OK")
                    writer.println("Content-Type: text/html; charset=utf-8")
                    writer.println("")
                    writer.println("<!DOCTYPE html>")
                    writer.println("<html>")
                    writer.println("<body>")
                    writer.println("<h1>Welcome to this webserver</h1>")
                    writer.println("<h3>These are your headers:</h3>")
                    writer.println("<ul>")
                    reader.lines().filter { line -> !line.isEmpty() }
                        .forEach { line -> writer.println("<li>$line</li>") }
                    writer.println("</ul>")
                    writer.println("</body>")
                    writer.println("</html>")
                    writer.flush()
                    connection.close()
                }
            }
        }
    }
}

fun main() {
    val PORT_NUMBER = 1250
    val server = ServerSocket(PORT_NUMBER)
    println("Log for server. Now we wait...")
    for (i in 0 until 50) {
        try {
            val connection = server.accept()
            println("Thread nr: ${i + 1} waiting for client")
            val t = ThreadWebServer(connection)
            t.start()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
