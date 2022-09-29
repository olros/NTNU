package exercise2

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*

fun main() {
  val ds = DatagramSocket(1250)
  val sc = Scanner(System.`in`)
  var m = true
  while (m) {
    val msg = sc.nextLine()
    val buf: ByteArray = msg.toByteArray()
    val dpSend = DatagramPacket(buf, buf.size, InetAddress.getByName("224.0.0.1"), 1251)
    ds.send(dpSend)
    if (msg == "") m = false
  }
}
