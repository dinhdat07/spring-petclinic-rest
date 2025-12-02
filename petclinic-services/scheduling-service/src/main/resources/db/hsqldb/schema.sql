DROP TABLE scheduling_appointment_allocations IF EXISTS;
DROP TABLE scheduling_slots IF EXISTS;

CREATE TABLE scheduling_slots (
  id INTEGER IDENTITY PRIMARY KEY,
  vet_id INTEGER NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  capacity INTEGER NOT NULL,
  booked_count INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT uk_scheduling_slot_vet_time UNIQUE (vet_id, start_time)
);

CREATE TABLE scheduling_appointment_allocations (
  id INTEGER IDENTITY PRIMARY KEY,
  appointment_id INTEGER NOT NULL UNIQUE,
  slot_id INTEGER NOT NULL,
  FOREIGN KEY (slot_id) REFERENCES scheduling_slots (id)
);
