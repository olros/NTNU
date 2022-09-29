import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*

fun main() {
    val address: InetAddress = InetAddress.getByName("localhost")
    val socket = DatagramSocket()
    val scanner = Scanner(System.`in`)
    var input = scanner.nextLine()
    println("Send a equation (ex: '1 + 1') to receieve the answer")
    socket.use {
        while (input != "Exit") {
            val response = sendMsg(input, socket, address)
            println("Answer: $response")
            input = scanner.nextLine()
        }
        sendMsg(input, socket, address)
    }
}

fun sendMsg(msg: String, socket: DatagramSocket, address: InetAddress): String {
    val buf = msg.toByteArray()
    var packet = DatagramPacket(buf, buf.size, address, 4445)
    socket.send(packet)
    packet = DatagramPacket(buf, buf.size)
    socket.receive(packet)
    return String(
        packet.data, 0, packet.length
    )
}