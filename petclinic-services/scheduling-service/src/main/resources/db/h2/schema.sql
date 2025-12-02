DROP TABLE IF EXISTS scheduling_appointment_allocations;
DROP TABLE IF EXISTS scheduling_slots;

CREATE TABLE IF NOT EXISTS scheduling_slots (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  vet_id INTEGER NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  capacity INTEGER NOT NULL,
  booked_count INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT uk_scheduling_slot_vet_time UNIQUE (vet_id, start_time)
);

CREATE TABLE IF NOT EXISTS scheduling_appointment_allocations (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  appointment_id INTEGER NOT NULL UNIQUE,
  slot_id INTEGER NOT NULL,
  FOREIGN KEY (slot_id) REFERENCES scheduling_slots(id)
);
