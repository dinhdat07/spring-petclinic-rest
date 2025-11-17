package org.springframework.samples.petclinic.notifications;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petclinic.notifications")
public class NotificationServiceProperties {

    /**
     * Toggles the notification microservice listener.
     */
    private boolean serviceEnabled = true;

    public boolean isServiceEnabled() {
        return serviceEnabled;
    }

    public void setServiceEnabled(boolean serviceEnabled) {
        this.serviceEnabled = serviceEnabled;
    }
}
