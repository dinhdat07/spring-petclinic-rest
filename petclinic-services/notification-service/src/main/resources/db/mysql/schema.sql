DROP TABLE IF EXISTS notification_log;

CREATE TABLE IF NOT EXISTS notification_log (
  id int(11) NOT NULL AUTO_INCREMENT,
  appointment_id int,
  event_type varchar(50),
  created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) engine=InnoDB;
