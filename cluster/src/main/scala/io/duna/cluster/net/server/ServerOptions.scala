package cluster.net.server

import io.duna.cluster.net.ssl.SslOptions

case class ServerOptions(hostname: String = "localhost",
                         port: Int = 7888,
                         transport: TransportProtocol,
                         sslOptions: Option[SslOptions] = None)

trait TransportProtocol

object TCP extends TransportProtocol
object UDP extends TransportProtocol
