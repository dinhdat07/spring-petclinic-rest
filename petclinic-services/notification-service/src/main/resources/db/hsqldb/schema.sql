DROP TABLE notification_log IF EXISTS;

CREATE TABLE notification_log (
  id INTEGER IDENTITY PRIMARY KEY,
  appointment_id INTEGER,
  event_type VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
