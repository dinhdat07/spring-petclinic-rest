package org.springframework.samples.petclinic.gateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "petclinic.jwt")
public class GatewaySecurityProperties {

    /**
     * Base64 encoded secret (same as modulith-main).
     */
    private String base64Secret;

    public String getBase64Secret() {
        return base64Secret;
    }

    public void setBase64Secret(String base64Secret) {
        this.base64Secret = base64Secret;
    }

    public SecretKeySpec getSecretKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Secret);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
}
