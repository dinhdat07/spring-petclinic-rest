package org.springframework.samples.petclinic.authentication.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.authentication.api.AuthenticationFacade;
import org.springframework.samples.petclinic.authentication.api.LoginCommand;
import org.springframework.samples.petclinic.authentication.api.TokenView;
import org.springframework.samples.petclinic.owners.app.self.OwnerRegistrationService;
import org.springframework.samples.petclinic.owners.app.self.RegisterOwnerCommand;
import org.springframework.samples.petclinic.owners.domain.Owner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationFacade authenticationFacade;
    private final OwnerRegistrationService ownerRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenView token = authenticationFacade.authenticate(new LoginCommand(request.username(), request.password()));
        return ResponseEntity.ok(new LoginResponse(token.accessToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterOwnerResponse> register(@Valid @RequestBody RegisterOwnerRequest request) {
        RegisterOwnerCommand command = new RegisterOwnerCommand(
            request.username(),
            request.password(),
            request.firstName(),
            request.lastName(),
            request.address(),
            request.city(),
            request.telephone()
        );
        Owner owner = ownerRegistrationService.register(command);
        RegisterOwnerResponse body = new RegisterOwnerResponse(owner.getId(), owner.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record LoginResponse(String accessToken) {}
}
