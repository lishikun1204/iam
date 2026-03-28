# JWK/签名密钥配置

默认情况下服务端会在启动时生成一对临时 RSA KeyPair 用于签发/校验 JWT。为了避免重启后历史 token 失效（尤其是 refresh token 相关联的授权记录），可以配置从 keystore 加载固定密钥。

## 配置项

```yaml
iam:
  security:
    issuer: http://localhost:8080
    jwk:
      key-store-location: file:/path/to/iam.p12
      key-store-password: changeit
      key-alias: iam
      key-password: changeit
      key-id: iam-kid
```

说明：
- key-store-location 支持 classpath: 与 file: 形式
- key-password 为空时会回退使用 key-store-password
- key-id 为空时会回退使用 key-alias

