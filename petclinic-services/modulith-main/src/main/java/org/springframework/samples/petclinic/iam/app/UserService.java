package org.springframework.samples.petclinic.iam.app;

import org.springframework.samples.petclinic.iam.domain.User;

public interface UserService {

    void saveUser(User user);

    boolean existsByUsername(String username);
}
