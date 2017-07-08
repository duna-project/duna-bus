package io.duna.cluster.config

import com.typesafe.config.Config

sealed abstract class SslOptions(val requireClientAuth: Boolean,
                                 val trustCertificateChain: String)

object SslOptions {
  def apply(config: Config): SslOptions = {
    require(config.hasPath("pem") || config.hasPath("jks"),
      "Either of `pem` or `jks` must be defined.")

    require(!(config.hasPath("pem") && config.hasPath("jks")),
      "Cannot define both `pem` and `jks` as SSL methods.")

    if (config.hasPath("pem")) {
      PemKeyCertOptions(config.getBoolean("requireClientAuth"),
        config.getString("trustCertificateChain"),
        config.getString("privateKey"),
        config.getString("certificate"),
        config.getString("password"))
    } else {
      KeyStoreOptions(config.getBoolean("requireClientAuth"),
        config.getString("trustCertificateChain"),
        config.getString("keystore"),
        config.getString("password"))
    }
  }
}

case class SelfSignedCertificate(override val requireClientAuth: Boolean = false,
                                 override val trustCertificateChain: String = null)
  extends SslOptions(requireClientAuth, trustCertificateChain)

case class KeyStoreOptions(override val requireClientAuth: Boolean = false,
                           override val trustCertificateChain: String = null,
                           path: String,
                           password: String)
  extends SslOptions(requireClientAuth, trustCertificateChain)

case class PemKeyCertOptions(override val requireClientAuth: Boolean = false,
                             override val trustCertificateChain: String = null,
                             keyPath: String,
                             certPath: String,
                             password: String)
  extends SslOptions(requireClientAuth, trustCertificateChain)