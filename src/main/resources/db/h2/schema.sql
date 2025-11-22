CREATE TABLE IF NOT EXISTS vets (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  email VARCHAR(255),
  username VARCHAR(20),
  CONSTRAINT fk_vet_user FOREIGN KEY (username) REFERENCES users(username),
  CONSTRAINT uq_vet_username UNIQUE (username)
);

CREATE INDEX idx_vets_last_name ON vets(last_name);

CREATE TABLE IF NOT EXISTS specialties (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(80) NOT NULL
);

CREATE INDEX idx_specialties_name ON specialties(name);

CREATE TABLE IF NOT EXISTS vet_specialties (
  vet_id INTEGER NOT NULL,
  specialty_id INTEGER NOT NULL,
  FOREIGN KEY (vet_id) REFERENCES vets(id) ON DELETE CASCADE,
  FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE CASCADE,
  UNIQUE (vet_id, specialty_id)
);

CREATE TABLE IF NOT EXISTS types (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(80) NOT NULL
);

CREATE INDEX idx_types_name ON types(name);

CREATE TABLE IF NOT EXISTS owners (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  address VARCHAR(255) NOT NULL,
  city VARCHAR(80) NOT NULL,
  telephone VARCHAR(20) NOT NULL,
  email VARCHAR(255),
  username VARCHAR(20),
  CONSTRAINT fk_owner_user FOREIGN KEY (username) REFERENCES users(username),
  CONSTRAINT uq_owner_username UNIQUE (username)
);

CREATE INDEX idx_owners_last_name ON owners(last_name);

CREATE TABLE IF NOT EXISTS pets (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  birth_date DATE NOT NULL,
  type_id INTEGER NOT NULL,
  owner_id INTEGER NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE CASCADE,
  FOREIGN KEY (type_id) REFERENCES types(id) ON DELETE CASCADE
);

CREATE INDEX idx_pets_name ON pets(name);

CREATE TABLE IF NOT EXISTS visits (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pet_id INTEGER NOT NULL,
  visit_date DATE NOT NULL,
  description VARCHAR(255) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
  vet_id INTEGER,
  FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
  FOREIGN KEY (vet_id) REFERENCES vets(id)
);

CREATE TABLE IF NOT EXISTS appointments (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  owner_id INTEGER NOT NULL,
  pet_id INTEGER NOT NULL,
  start_time TIMESTAMP NOT NULL,
  status VARCHAR(20) NOT NULL,
  notes VARCHAR(255),
  triage_notes VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES owners(id) ON DELETE CASCADE,
  FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
  vet_id INTEGER,
  visit_id INTEGER UNIQUE,
  FOREIGN KEY (vet_id) REFERENCES vets(id),
  FOREIGN KEY (visit_id) REFERENCES visits(id)
);

CREATE INDEX idx_appointments_owner ON appointments(owner_id);
CREATE INDEX idx_appointments_pet ON appointments(pet_id);
CREATE INDEX idx_appointments_vet ON appointments(vet_id);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_visits_vet ON visits(vet_id);

CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(20) NOT NULL PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS roles (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  username VARCHAR(20) NOT NULL,
  role VARCHAR(20) NOT NULL,
  UNIQUE (role, username),
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

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
