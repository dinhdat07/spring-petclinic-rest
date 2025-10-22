package org.springframework.samples.petclinic.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.cors")
public class CorsProps {
    // default config
    private String[] allowedOrigins = new String[0];
    private String[] allowedMethods = {"GET","POST","PUT","DELETE","OPTIONS"};
    private String[] allowedHeaders = {"Content-Type","Authorization","X-Requested-With"};
    private String[] exposedHeaders = {"Location","Link","X-Rate-Limit-Remaining"};
    private boolean allowCredentials = true;
    private long maxAge = 3600L;

    public String[] getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(String[] allowedOrigins) { this.allowedOrigins = allowedOrigins; }

    public String[] getAllowedMethods() { return allowedMethods; }
    public void setAllowedMethods(String[] allowedMethods) { this.allowedMethods = allowedMethods; }

    public String[] getAllowedHeaders() { return allowedHeaders; }
    public void setAllowedHeaders(String[] allowedHeaders) { this.allowedHeaders = allowedHeaders; }

    public String[] getExposedHeaders() { return exposedHeaders; }
    public void setExposedHeaders(String[] exposedHeaders) { this.exposedHeaders = exposedHeaders; }

    public boolean isAllowCredentials() { return allowCredentials; }
    public void setAllowCredentials(boolean allowCredentials) { this.allowCredentials = allowCredentials; }

    public long getMaxAge() { return maxAge; }
    public void setMaxAge(long maxAge) { this.maxAge = maxAge; }
}
