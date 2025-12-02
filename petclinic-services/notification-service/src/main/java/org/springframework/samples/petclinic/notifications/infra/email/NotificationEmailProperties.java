package org.springframework.samples.petclinic.notifications.infra.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petclinic.notifications.email")
public class NotificationEmailProperties {

    private boolean enabled = false;
    private String from = "no-reply@petclinic.test";
    private String ownerRecipient = "owner@example.com";
    private String vetRecipient = "vet@example.com";
    private String subjectConfirmed = "Appointment #{appointmentId} confirmed";
    private String subjectVisitLinked = "Visit #{visitId} linked to appointment #{appointmentId}";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getOwnerRecipient() {
        return ownerRecipient;
    }

    public void setOwnerRecipient(String ownerRecipient) {
        this.ownerRecipient = ownerRecipient;
    }

    public String getVetRecipient() {
        return vetRecipient;
    }

    public void setVetRecipient(String vetRecipient) {
        this.vetRecipient = vetRecipient;
    }

    public String getSubjectConfirmed() {
        return subjectConfirmed;
    }

    public void setSubjectConfirmed(String subjectConfirmed) {
        this.subjectConfirmed = subjectConfirmed;
    }

    public String getSubjectVisitLinked() {
        return subjectVisitLinked;
    }

    public void setSubjectVisitLinked(String subjectVisitLinked) {
        this.subjectVisitLinked = subjectVisitLinked;
    }
}
