DROP TABLE vet_specialties IF EXISTS;
DROP TABLE vets IF EXISTS;
DROP TABLE specialties IF EXISTS;
DROP TABLE visits IF EXISTS;
DROP TABLE pets IF EXISTS;
DROP TABLE types IF EXISTS;
DROP TABLE owners IF EXISTS;
DROP TABLE roles IF EXISTS;
DROP TABLE users IF EXISTS;

CREATE  TABLE users (
  username    VARCHAR(20) NOT NULL ,
  password    VARCHAR(60) NOT NULL ,
  enabled     BOOLEAN DEFAULT TRUE NOT NULL ,
  PRIMARY KEY (username)
);

CREATE TABLE vets (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR(30),
  email      VARCHAR(255),
  username   VARCHAR(20) UNIQUE
);
ALTER TABLE vets ADD CONSTRAINT fk_vets_users FOREIGN KEY (username) REFERENCES users (username);
CREATE INDEX vets_last_name ON vets (last_name);

CREATE TABLE specialties (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX specialties_name ON specialties (name);

CREATE TABLE vet_specialties (
  vet_id       INTEGER NOT NULL,
  specialty_id INTEGER NOT NULL
);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE vet_specialties ADD CONSTRAINT fk_vet_specialties_specialties FOREIGN KEY (specialty_id) REFERENCES specialties (id);

CREATE TABLE types (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(80)
);
CREATE INDEX types_name ON types (name);

CREATE TABLE owners (
  id         INTEGER IDENTITY PRIMARY KEY,
  first_name VARCHAR(30),
  last_name  VARCHAR_IGNORECASE(30),
  address    VARCHAR(255),
  city       VARCHAR(80),
  telephone  VARCHAR(20),
  email      VARCHAR(255),
  username   VARCHAR(20) UNIQUE
);
ALTER TABLE owners ADD CONSTRAINT fk_owner_user FOREIGN KEY (username) REFERENCES users (username);
CREATE INDEX owners_last_name ON owners (last_name);

CREATE TABLE pets (
  id         INTEGER IDENTITY PRIMARY KEY,
  name       VARCHAR(30),
  birth_date DATE,
  type_id    INTEGER NOT NULL,
  owner_id   INTEGER NOT NULL
);
ALTER TABLE pets ADD CONSTRAINT fk_pets_owners FOREIGN KEY (owner_id) REFERENCES owners (id);
ALTER TABLE pets ADD CONSTRAINT fk_pets_types FOREIGN KEY (type_id) REFERENCES types (id);
CREATE INDEX pets_name ON pets (name);

CREATE TABLE visits (
  id          INTEGER IDENTITY PRIMARY KEY,
  pet_id      INTEGER NOT NULL,
  visit_date  DATE,
  description VARCHAR(255),
  status      VARCHAR(20) DEFAULT 'SCHEDULED' NOT NULL,
  vet_id      INTEGER
);
ALTER TABLE visits ADD CONSTRAINT fk_visits_pets FOREIGN KEY (pet_id) REFERENCES pets (id);
ALTER TABLE visits ADD CONSTRAINT fk_visits_vets FOREIGN KEY (vet_id) REFERENCES vets (id);
CREATE INDEX visits_pet_id ON visits (pet_id);
CREATE INDEX visits_vet_id ON visits (vet_id);

CREATE TABLE appointments (
  id          INTEGER IDENTITY PRIMARY KEY,
  owner_id    INTEGER NOT NULL,
  pet_id      INTEGER NOT NULL,
  start_time  TIMESTAMP NOT NULL,
  status      VARCHAR(20) NOT NULL,
  notes       VARCHAR(255),
  triage_notes VARCHAR(255),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  vet_id      INTEGER,
  visit_id    INTEGER UNIQUE
);
ALTER TABLE appointments ADD CONSTRAINT fk_appointments_owner FOREIGN KEY (owner_id) REFERENCES owners (id);
ALTER TABLE appointments ADD CONSTRAINT fk_appointments_pet FOREIGN KEY (pet_id) REFERENCES pets (id);
ALTER TABLE appointments ADD CONSTRAINT fk_appointments_vet FOREIGN KEY (vet_id) REFERENCES vets (id);
ALTER TABLE appointments ADD CONSTRAINT fk_appointments_visit FOREIGN KEY (visit_id) REFERENCES visits (id);
CREATE INDEX idx_appointments_owner ON appointments (owner_id);
CREATE INDEX idx_appointments_pet ON appointments (pet_id);
CREATE INDEX idx_appointments_vet ON appointments (vet_id);
CREATE INDEX idx_appointments_status ON appointments (status);

CREATE TABLE roles (
  id              INTEGER IDENTITY PRIMARY KEY,
  username        VARCHAR(20) NOT NULL,
  role            VARCHAR(20) NOT NULL
);
ALTER TABLE roles ADD CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username);
CREATE INDEX fk_username_idx ON roles (username);

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

