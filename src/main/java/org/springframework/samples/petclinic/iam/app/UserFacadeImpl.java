package org.springframework.samples.petclinic.iam.app;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.iam.api.UserAuthView;
import org.springframework.samples.petclinic.iam.api.UserFacade;
import org.springframework.samples.petclinic.iam.domain.User;
import org.springframework.samples.petclinic.iam.infra.jpa.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserFacadeImpl implements UserFacade {

    private final UserJpaRepository userRepository;

    @Override
    public Optional<UserAuthView> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(this::toView);
    }

    private UserAuthView toView(User user) {
        return new UserAuthView(
            user.getUsername(),
            user.getPassword(),
            Boolean.TRUE.equals(user.getEnabled()),
            user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toUnmodifiableSet())
        );
    }
}
