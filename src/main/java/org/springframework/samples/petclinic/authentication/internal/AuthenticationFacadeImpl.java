package org.springframework.samples.petclinic.authentication.internal;

import org.springframework.samples.petclinic.authentication.api.AuthenticationFacade;
import org.springframework.samples.petclinic.authentication.api.LoginCommand;
import org.springframework.samples.petclinic.authentication.api.TokenView;
import org.springframework.samples.petclinic.authentication.util.SecurityUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

@Service
@Transactional(readOnly = true)
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    private final AuthenticationManager authenticationManager;
    private final SecurityUtil securityUtil;

    public AuthenticationFacadeImpl(AuthenticationManagerBuilder authenticationManagerBuilder,
                                    SecurityUtil securityUtil) {
        this.authenticationManager = authenticationManagerBuilder.getObject();
        this.securityUtil = securityUtil;
    }

    @Override
    public TokenView authenticate(@Valid LoginCommand command) {
        Authentication authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                command.username(),
                command.password())
        );
        String token = securityUtil.createToken(authentication);
        return new TokenView(token);
    }
}

