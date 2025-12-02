DROP TABLE IF EXISTS scheduling_appointment_allocations;
DROP TABLE IF EXISTS scheduling_slots;

CREATE TABLE IF NOT EXISTS scheduling_slots (
  id int(11) NOT NULL AUTO_INCREMENT,
  vet_id int NOT NULL,
  start_time datetime NOT NULL,
  end_time datetime NOT NULL,
  capacity int NOT NULL,
  booked_count int NOT NULL,
  status varchar(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_scheduling_slot_vet_time (vet_id, start_time)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS scheduling_appointment_allocations (
  id int(11) NOT NULL AUTO_INCREMENT,
  appointment_id int NOT NULL UNIQUE,
  slot_id int NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_scheduling_slot FOREIGN KEY (slot_id) REFERENCES scheduling_slots (id)
) engine=InnoDB;
