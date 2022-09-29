import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.charset.StandardCharsets

object UDPServer {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        UDPServerThread().start()
    }
}

class UDPServerThread() : Thread() {
    private var socket: DatagramSocket? = null
    private var running = false
    private val buf = ByteArray(256)
    override fun run() {
        socket.use {
            running = true

            while (running) {
                val packet = DatagramPacket(buf, buf.size)
                socket!!.receive(packet)
                val address = packet.address
                val port = packet.port
                val received = String(packet.data, packet.offset, packet.length, StandardCharsets.UTF_8)
                println("Received: $received")
                if (received == "Exit") {
                    running = false
                    break
                }
                val response = calc(received).toString()
                val sendPacket = DatagramPacket(response.toByteArray(), response.toByteArray().size, address, port)
                socket!!.send(sendPacket)
            }
        }
    }

    init {
        socket = DatagramSocket(4445)
    }
}


private fun calc(equation: String): Int {
    try {
        println("'$equation'")
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
    } catch (e: Exception) {
        e.printStackTrace()
        return -1
    }
}