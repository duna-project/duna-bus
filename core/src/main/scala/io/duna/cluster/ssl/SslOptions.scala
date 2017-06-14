package io.duna.cluster.ssl

sealed abstract case class SslOptions(client: Boolean = false,
                                      requireClientAuth: Boolean = false)

object SelfSignedCertificate extends SslOptions

case class KeyStoreOptions(override val client: Boolean,
                           override val requireClientAuth: Boolean = false,
                           path: String,
                           password: String) extends SslOptions(client, requireClientAuth)

case class PemKeyCertOptions(override val client: Boolean,
                             override val requireClientAuth: Boolean = false,
                             keyPath: String,
                             certPath: String,
                             password: String) extends SslOptions(client, requireClientAuth)
