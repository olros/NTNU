package exercise2

import java.net.InetSocketAddress
import java.nio.channels.AsynchronousSocketChannel

suspend fun runServer() {
  val server = asyncOpenServer()
  server.asyncBind(InetSocketAddress("127.0.0.1", 1250))
  val worker: AsynchronousSocketChannel = server.asyncAccept()
  val instructions = "Write a message and i will return it to you"
  sendMessage(instructions, worker)
  var msg = readMessage(worker)
  while (msg != "") {
    sendMessage(msg, worker)
    msg = readMessage(worker)
  }
}

suspend fun main() {
  runServer()
}
