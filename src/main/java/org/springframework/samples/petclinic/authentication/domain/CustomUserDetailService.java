package org.springframework.samples.petclinic.authentication.domain;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.iam.api.UserAuthView;
import org.springframework.samples.petclinic.iam.api.UserFacade;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("userDetailService")
public class CustomUserDetailService implements UserDetailsService {

    private final UserFacade userFacade;

    public CustomUserDetailService(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthView user = this.userFacade.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<SimpleGrantedAuthority> authorities = user.roles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toUnmodifiableSet());

        return new User(
            user.username(),
            user.password(),
            user.enabled(),
            true,
            true,
            true,
            authorities
        );
    }
}
