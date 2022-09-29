package exercise2

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun main() {
  val ds = DatagramSocket(1250)
  var msg = " "
  while (msg != "") {
    var returnInfo = receiveServer(ds)
    val eq = returnInfo.first
    if (eq != null) {
      eq.ans = calcEquation(eq)
    } else println("Something went wrong")
    if (eq != null) {
      serializeEquation(eq).run {
        if (this != null) {
          val dpSend = DatagramPacket(this, this.size, returnInfo.third, returnInfo.second)
          ds.send(dpSend)
        } else println("Something went wrong try agian")
      }
    }
  }
}

fun receiveServer(sokcet: DatagramSocket): Triple<Equation?, Int, InetAddress> {
  var receive = ByteArray(6000)
  val recvPacket = DatagramPacket(receive, receive.size)
  sokcet.receive(recvPacket)
  val obj: Equation? = deserializeEquation(receive)
  return Triple(obj, recvPacket.port, recvPacket.address)
}

fun calcEquation(eq: Equation): String {
  if (eq.op == "/" && eq.nr2 == "0") return "You can't divide by zero"
  when (eq.op) {
    "+" -> return (eq.nr1.toDouble() + eq.nr2.toDouble()).toString()
    "-" -> return (eq.nr1.toDouble() - eq.nr2.toDouble()).toString()
    "*" -> return (eq.nr1.toDouble() * eq.nr2.toDouble()).toString()
    "/" -> return (eq.nr1.toDouble() / eq.nr2.toDouble()).toString()
  }
  return "Wrong format"
}
