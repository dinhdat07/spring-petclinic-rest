package org.springframework.samples.petclinic.appointments.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petclinic.messaging.appointments")
public class AppointmentMessagingProperties {

    private String exchange = "petclinic.appointments.exchange";
    private String confirmedRoutingKey = "appointments.confirmed";
    private String visitLinkedRoutingKey = "appointments.visit-linked";
    private String deadLetterExchange = "petclinic.appointments.dlx";
    private String notificationsQueue = "appointments.notifications.q";
    private String notificationsDlq = "appointments.notifications.dlq";
    private String availabilityQueue = "appointments.availability.q";
    private String availabilityDlq = "appointments.availability.dlq";
    private boolean internalNotificationsConsumerEnabled = true;
    private boolean internalAvailabilityConsumerEnabled = true;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getConfirmedRoutingKey() {
        return confirmedRoutingKey;
    }

    public void setConfirmedRoutingKey(String confirmedRoutingKey) {
        this.confirmedRoutingKey = confirmedRoutingKey;
    }

    public String getVisitLinkedRoutingKey() {
        return visitLinkedRoutingKey;
    }

    public void setVisitLinkedRoutingKey(String visitLinkedRoutingKey) {
        this.visitLinkedRoutingKey = visitLinkedRoutingKey;
    }

    public String getDeadLetterExchange() {
        return deadLetterExchange;
    }

    public void setDeadLetterExchange(String deadLetterExchange) {
        this.deadLetterExchange = deadLetterExchange;
    }

    public String getNotificationsQueue() {
        return notificationsQueue;
    }

    public void setNotificationsQueue(String notificationsQueue) {
        this.notificationsQueue = notificationsQueue;
    }

    public String getNotificationsDlq() {
        return notificationsDlq;
    }

    public void setNotificationsDlq(String notificationsDlq) {
        this.notificationsDlq = notificationsDlq;
    }

    public String getAvailabilityQueue() {
        return availabilityQueue;
    }

    public void setAvailabilityQueue(String availabilityQueue) {
        this.availabilityQueue = availabilityQueue;
    }

    public String getAvailabilityDlq() {
        return availabilityDlq;
    }

    public void setAvailabilityDlq(String availabilityDlq) {
        this.availabilityDlq = availabilityDlq;
    }

    public boolean isInternalNotificationsConsumerEnabled() {
        return internalNotificationsConsumerEnabled;
    }

    public void setInternalNotificationsConsumerEnabled(boolean internalNotificationsConsumerEnabled) {
        this.internalNotificationsConsumerEnabled = internalNotificationsConsumerEnabled;
    }

    public boolean isInternalAvailabilityConsumerEnabled() {
        return internalAvailabilityConsumerEnabled;
    }

    public void setInternalAvailabilityConsumerEnabled(boolean internalAvailabilityConsumerEnabled) {
        this.internalAvailabilityConsumerEnabled = internalAvailabilityConsumerEnabled;
    }
}
