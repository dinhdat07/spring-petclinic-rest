package org.springframework.samples.petclinic.iam.infra.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.iam.domain.User;

public interface UserJpaRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);
}

