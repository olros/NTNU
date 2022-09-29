package exercise2

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*

fun main() {
  val sc = Scanner(System.`in`)
  val ds = DatagramSocket()
  var msg = sc.nextLine()
  while (msg != "") {
    val eq = equationFromString(msg)
    serializeEquation(eq).run {
      if (this != null) {
        val dpSend = DatagramPacket(this, this.size, InetAddress.getLocalHost(), 1250)
        ds.send(dpSend)
      } else println("Something went wrong try agian")
    }
    println(receiveAns(ds)?.ans ?: "SomeThing went wrong")
    msg = sc.nextLine()
  }
}

fun receiveAns(sokcet: DatagramSocket): Equation? {
  var receive = ByteArray(6000)
  val dpReceive = DatagramPacket(receive, receive.size, InetAddress.getLocalHost(), 1250)
  sokcet.receive(dpReceive)
  val obj: Equation? = deserializeEquation(receive)
  return obj
}
