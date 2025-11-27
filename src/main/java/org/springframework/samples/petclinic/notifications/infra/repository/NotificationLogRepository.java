package org.springframework.samples.petclinic.notifications.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.notifications.domain.NotificationLog;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
