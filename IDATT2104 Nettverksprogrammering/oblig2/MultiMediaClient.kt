package exercise2

import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket

fun main() {
  val ds = MulticastSocket(1251)
  var receive = ByteArray(6000)
  val group = InetAddress.getByName("224.0.0.1")
  ds.joinGroup(group)
  while (true) {
    val dpReceive = DatagramPacket(receive, receive.size)
    ds.receive(dpReceive)
    val msg = String(receive)
    println(msg)
    receive = ByteArray(6000)
    if (msg.isBlank()) break
  }
  ds.leaveGroup(group)
  ds.close()
}
