package io.duna.cluster.net.ssl

import java.io.{File, FileInputStream}
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory

import io.duna.cluster.config.{KeyStoreOptions, PemKeyCertOptions, SelfSignedCertificate, SslOptions}
import io.netty.handler.ssl.{ClientAuth, SslContextBuilder, SslProvider, SslContext => NettySslContext}

object SslContext {

  def forClient(sslOptions: SslOptions): Option[NettySslContext] = {
    if (!sslOptions.requireClientAuth)
      return Some(SslContextBuilder
        .forClient()
        .trustManager(new File(sslOptions.trustCertificateChain))
        .build())

    sslOptions match {
      case _: SelfSignedCertificate =>
        val certChain = new io.netty.handler.ssl.util.SelfSignedCertificate()
        Some(
          SslContextBuilder
            .forClient()
            .keyManager(certChain.certificate(), certChain.privateKey())
            .trustManager(new File(sslOptions.trustCertificateChain))
            .build())
      case m: KeyStoreOptions =>
        val keystore = KeyStore.getInstance(KeyStore.getDefaultType)
        keystore.load(new FileInputStream(m.path), m.password.toCharArray)

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
        keyManagerFactory.init(keystore, m.password.toCharArray)

        Some(SslContextBuilder
          .forClient()
          .keyManager(keyManagerFactory)
          .trustManager(new File(sslOptions.trustCertificateChain))
          .build())
      case m: PemKeyCertOptions =>
        Some(SslContextBuilder
          .forClient()
          .sslProvider(SslProvider.OPENSSL_REFCNT)
          .trustManager(new File(sslOptions.trustCertificateChain))
          .keyManager(new File(m.certPath), new File(m.keyPath), m.password)
          .build())
      case _ => None
    }
  }

  def forServer(sslOptions: SslOptions): Option[NettySslContext] =
    sslOptions match {
      case _: SelfSignedCertificate =>
        val certChain = new io.netty.handler.ssl.util.SelfSignedCertificate()
        Some(SslContextBuilder
          .forServer(certChain.certificate(), certChain.privateKey())
          .sslProvider(SslProvider.JDK)
          .trustManager(new File(sslOptions.trustCertificateChain))
          .clientAuth(
            if (sslOptions.requireClientAuth) ClientAuth.REQUIRE
            else ClientAuth.OPTIONAL)
          .build())
      case m: KeyStoreOptions =>
        val keystore = KeyStore.getInstance(KeyStore.getDefaultType)
        keystore.load(new FileInputStream(m.path), m.password.toCharArray)

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
        keyManagerFactory.init(keystore, m.password.toCharArray)

        Some(SslContextBuilder
          .forServer(keyManagerFactory)
          .trustManager(new File(sslOptions.trustCertificateChain))
          .clientAuth(
            if (m.requireClientAuth) ClientAuth.REQUIRE
            else ClientAuth.OPTIONAL)
          .build()
        )
      case m: PemKeyCertOptions =>
        Some(SslContextBuilder
          .forServer(new File(m.certPath), new File(m.keyPath), m.password)
          .trustManager(new File(sslOptions.trustCertificateChain))
          .sslProvider(SslProvider.OPENSSL_REFCNT)
          .clientAuth(
            if (m.requireClientAuth) ClientAuth.REQUIRE
            else ClientAuth.OPTIONAL)
          .build()
        )
      case _ => None
    }
}
