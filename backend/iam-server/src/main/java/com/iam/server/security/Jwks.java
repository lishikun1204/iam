package com.iam.server.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

public final class Jwks {
  private Jwks() {
  }

    /**
   * 生成一个用于JWT签名的RSA密钥对。
   *
   * @return 包含2048位长度的公钥和私钥的KeyPair对象
   */
  public static KeyPair generateRsaKeyPair() {
    KeyPairGenerator keyPairGenerator;
    // 尝试获取RSA算法的密钥对生成器实例，如果JVM不支持则抛出运行时异常
    try {
      keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException(ex);
    }
    keyPairGenerator.initialize(2048);
    return keyPairGenerator.generateKeyPair();
  }

    /**
   * 生成一个包含RSA公钥和私钥的JWK Set (JSON Web Key Set)。
   * 该方法首先生成一个新的RSA密钥对，然后将其封装为符合RFC 7517标准的JWK对象，并为其分配一个唯一的ID。
   *
   * @return 包含单个RSAKey的JWKSet对象，可用于JWT签名和验证
   */
  public static JWKSet generateRsaJwkSet() {
    KeyPair pair = generateRsaKeyPair();
    RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
    // 使用公钥和私钥构建一个RSAKey对象，并设置一个随机生成的唯一键ID
    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();
    return new JWKSet(rsaKey);
  }
}

