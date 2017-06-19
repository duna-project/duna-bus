package io.duna.cluster.net.ssl

import java.io.{File, FileInputStream}
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
        val certChain = new io.netty.handler.ssl.util.SelfSignedCertificate()
        Some(SslContextBuilder
          .forServer(certChain.certificate(), certChain.privateKey())
          .sslProvider(SslProvider.JDK)
          .build()
        )
      case m: KeyStoreOptions =>
        val keystore = KeyStore.getInstance(KeyStore.getDefaultType)
        keystore.load(new FileInputStream(m.path), m.password.toCharArray)

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
        keyManagerFactory.init(keystore, m.password.toCharArray)

        Some(SslContextBuilder
          .forServer(keyManagerFactory)
          .clientAuth(
            if (m.requireClientAuth) ClientAuth.REQUIRE
            else ClientAuth.OPTIONAL)
          .build()
        )
      case m: PemKeyCertOptions =>
        Some(SslContextBuilder
          .forServer(new File(m.certPath), new File(m.keyPath), m.password)
          .sslProvider(SslProvider.OPENSSL_REFCNT)
          .clientAuth(
            if (m.requireClientAuth) ClientAuth.REQUIRE
            else ClientAuth.OPTIONAL)
          .build()
        )
      case _ => None
    }
}
