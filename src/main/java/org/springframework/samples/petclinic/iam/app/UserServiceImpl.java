package org.springframework.samples.petclinic.iam.app;

import org.springframework.samples.petclinic.iam.domain.Role;
import org.springframework.samples.petclinic.iam.domain.User;
import org.springframework.samples.petclinic.iam.infra.jpa.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("User must have at least a role set!");
        }

        for (Role role : user.getRoles()) {
            if (!role.getName().startsWith("ROLE_")) {
                role.setName("ROLE_" + role.getName());
            }

            if (role.getUser() == null) {
                role.setUser(user);
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }
}
