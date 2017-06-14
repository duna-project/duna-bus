package io.duna.cluster.ssl

import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.{KeyManagerFactory, SSLContext}

import io.netty.handler.ssl.{ClientAuth, JdkSslContext, SslContextBuilder, SslProvider, SslContext => NettySslContext}

object SslContext {

  def apply(sslOptions: Option[SslOptions]): Option[NettySslContext] =
    if (sslOptions.isDefined) createContext(sslOptions.get)
    else None

  private def createContext(sslOptions: SslOptions): Option[NettySslContext] =
    sslOptions match {
      case _: SelfSignedCertificate.type =>
        val cert = new io.netty.handler.ssl.util.SelfSignedCertificate()
        Some(
          SslContextBuilder.forServer(cert.certificate(), cert.privateKey())
            .sslProvider(SslProvider.JDK)
            .build()
        )
      case m: KeyStoreOptions =>
        val serverContext = SSLContext.getInstance("TLS")
        val keystore = KeyStore.getInstance("JKS")

        keystore.load(new FileInputStream(m.path), m.password.toCharArray)

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
        keyManagerFactory.init(keystore, m.password.toCharArray)

        serverContext.init(keyManagerFactory.getKeyManagers, null, null)

        Some(new JdkSslContext(serverContext, m.client,
          if (m.requireClientAuth) ClientAuth.REQUIRE
          else ClientAuth.OPTIONAL
        ))
      case m: PemKeyCertOptions =>
        None
      case _ => None
    }
}
