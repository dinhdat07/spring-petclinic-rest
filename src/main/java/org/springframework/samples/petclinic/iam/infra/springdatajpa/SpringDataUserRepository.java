package org.springframework.samples.petclinic.iam.infra.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.iam.domain.User;
import org.springframework.samples.petclinic.iam.infra.UserRepository;

@org.springframework.stereotype.Repository
@Profile("spring-data-jpa")
public interface SpringDataUserRepository extends UserRepository, Repository<User, String>  {

}
