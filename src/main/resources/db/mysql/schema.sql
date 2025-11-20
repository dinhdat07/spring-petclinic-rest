CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(20) NOT NULL ,
  password VARCHAR(60) NOT NULL ,
  enabled TINYINT NOT NULL DEFAULT 1 ,
  PRIMARY KEY (username)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS vets (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  username VARCHAR(20) UNIQUE,
  INDEX(last_name),
  CONSTRAINT fk_vet_user FOREIGN KEY (username) REFERENCES users(username)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS specialties (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80),
  INDEX(name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS vet_specialties (
  vet_id INT(4) UNSIGNED NOT NULL,
  specialty_id INT(4) UNSIGNED NOT NULL,
  FOREIGN KEY (vet_id) REFERENCES vets(id),
  FOREIGN KEY (specialty_id) REFERENCES specialties(id),
  UNIQUE (vet_id,specialty_id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS types (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80),
  INDEX(name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS owners (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  address VARCHAR(255),
  city VARCHAR(80),
  telephone VARCHAR(20),
  username VARCHAR(20) UNIQUE,
  INDEX(last_name),
  CONSTRAINT fk_owner_user FOREIGN KEY (username) REFERENCES users(username)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS pets (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30),
  birth_date DATE,
  type_id INT(4) UNSIGNED NOT NULL,
  owner_id INT(4) UNSIGNED NOT NULL,
  INDEX(name),
  FOREIGN KEY (owner_id) REFERENCES owners(id),
  FOREIGN KEY (type_id) REFERENCES types(id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS visits (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  pet_id INT(4) UNSIGNED NOT NULL,
  visit_date DATE,
  description VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
  vet_id INT(4) UNSIGNED,
  FOREIGN KEY (pet_id) REFERENCES pets(id),
  FOREIGN KEY (vet_id) REFERENCES vets(id),
  INDEX idx_visits_vet (vet_id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS appointments (
  id INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  owner_id INT(4) UNSIGNED NOT NULL,
  pet_id INT(4) UNSIGNED NOT NULL,
  start_time DATETIME NOT NULL,
  status VARCHAR(20) NOT NULL,
  notes VARCHAR(255),
  triage_notes VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES owners(id),
  FOREIGN KEY (pet_id) REFERENCES pets(id),
  vet_id INT(4) UNSIGNED,
  visit_id INT(4) UNSIGNED UNIQUE,
  FOREIGN KEY (vet_id) REFERENCES vets(id),
  FOREIGN KEY (visit_id) REFERENCES visits(id),
  INDEX idx_appointments_owner (owner_id),
  INDEX idx_appointments_pet (pet_id),
  INDEX idx_appointments_vet (vet_id),
  INDEX idx_appointments_status (status)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS roles (
  id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(20) NOT NULL,
  role varchar(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uni_username_role (role,username),
  KEY fk_username_idx (username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username)
) engine=InnoDB;

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
