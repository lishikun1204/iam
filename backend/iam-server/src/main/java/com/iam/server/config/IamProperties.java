package com.iam.server.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * IAM应用程序配置属性类
 * 通过@ConfigurationProperties绑定以"iam"为前缀的配置项
 */
@ConfigurationProperties(prefix = "iam")
public class IamProperties {
    private final Security security = new Security();

    /**
     * 获取安全配置对象
     *
     * @return 返回Security配置实例，包含issuer和cors等子配置
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "ConfigurationProperties binding requires nested object exposure")
    public Security getSecurity() {
        return security;
    }

    /**
     * 安全相关配置的内部类
     * 包含JWT签发者(issuer)和CORS跨域设置
     */
    public static class Security {
        private String issuer;
        private final Cors cors = new Cors();
        private final Jwk jwk = new Jwk();

        /**
         * 获取JWT令牌的签发者(issuer)
         *
         * @return 签发者字符串
         */
        public String getIssuer() {
            return issuer;
        }

        /**
         * 设置JWT令牌的签发者(issuer)
         *
         * @param issuer 签发者字符串
         */
        public void setIssuer(final String issuer) {
            this.issuer = issuer;
        }

        /**
         * 获取CORS跨域配置对象
         *
         * @return 返回Cors配置实例
         */
        @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "ConfigurationProperties binding requires nested object exposure")
        public Cors getCors() {
            return cors;
        }

        @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "ConfigurationProperties binding requires nested object exposure")
        public Jwk getJwk() {
            return jwk;
        }
    }

    public static class Jwk {
        private String keyStoreLocation;
        private String keyStorePassword;
        private String keyAlias;
        private String keyPassword;
        private String keyId;

        public String getKeyStoreLocation() {
            return keyStoreLocation;
        }

        public void setKeyStoreLocation(final String keyStoreLocation) {
            this.keyStoreLocation = keyStoreLocation;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public void setKeyStorePassword(final String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getKeyAlias() {
            return keyAlias;
        }

        public void setKeyAlias(final String keyAlias) {
            this.keyAlias = keyAlias;
        }

        public String getKeyPassword() {
            return keyPassword;
        }

        public void setKeyPassword(final String keyPassword) {
            this.keyPassword = keyPassword;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(final String keyId) {
            this.keyId = keyId;
        }
    }

    /**
     * CORS跨域资源共享配置内部类
     * 定义了允许的源列表(allowedOrigins)
     */
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>();

        /**
         * 获取允许的源列表
         *
         * @return 返回允许进行跨域请求的源地址列表
         */
        public List<String> getAllowedOrigins() {
            return allowedOrigins != null ? List.copyOf(allowedOrigins) : List.of();
        }

        /**
         * 设置允许的源列表
         *
         * @param allowedOrigins 允许进行跨域请求的源地址列表
         */
        public void setAllowedOrigins(final List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins != null ? new ArrayList<>(allowedOrigins) : null;
        }
    }
}

