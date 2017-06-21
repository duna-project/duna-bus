package io.duna.cluster.net.ssl

sealed abstract class SslOptions(client: Boolean = false,
                                 requireClientAuth: Boolean = false)

object SelfSignedCertificate extends SslOptions

case class KeyStoreOptions(client: Boolean,
                           requireClientAuth: Boolean = false,
                           path: String,
                           password: String) extends SslOptions(client, requireClientAuth)

case class PemKeyCertOptions(client: Boolean,
                             requireClientAuth: Boolean = false,
                             keyPath: String,
                             certPath: String,
                             password: String) extends SslOptions(client, requireClientAuth)
