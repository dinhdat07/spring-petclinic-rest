package org.springframework.samples.petclinic.owners.app.self;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.iam.app.UserService;
import org.springframework.samples.petclinic.iam.domain.Role;
import org.springframework.samples.petclinic.iam.domain.User;
import org.springframework.samples.petclinic.owners.app.owner.OwnerService;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.samples.petclinic.platform.props.Roles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerRegistrationService {

    private final OwnerService ownerService;
    private final UserService userService;
    private final Roles roles;

    @Transactional
    public Owner register(RegisterOwnerCommand command) {
        ownerService.findByUsername(command.username())
            .ifPresent(existing -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Owner profile already exists for username.");
            });

        if (userService.existsByUsername(command.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use.");
        }

        User user = new User();
        user.setUsername(command.username());
        user.setPassword(command.password());
        user.setEnabled(true);

        Role role = new Role();
        role.setName(roles.OWNER);

        user.setRoles(Set.of(role));
        userService.saveUser(user);

        Owner owner = new Owner();
        owner.setFirstName(command.firstName());
        owner.setLastName(command.lastName());
        owner.setAddress(command.address());
        owner.setCity(command.city());
        owner.setTelephone(command.telephone());
        owner.setUsername(command.username());

        ownerService.save(owner);
        return owner;
    }
}

