package exercise2

import java.net.InetSocketAddress
import java.util.*

suspend fun run() {
  val client = asyncOpen()
  val hostAddress = InetSocketAddress("localhost", 1250)
  client.asyncConnect(hostAddress)
  println(readMessage(client))
  val readFromCommandLine = Scanner(System.`in`)
  var msg = readFromCommandLine.nextLine()
  while (msg != "") {
    sendMessage(msg, client)
    println(readMessage(client))
    msg = readFromCommandLine.nextLine()
  }
}

suspend fun main() {
  run()
}
