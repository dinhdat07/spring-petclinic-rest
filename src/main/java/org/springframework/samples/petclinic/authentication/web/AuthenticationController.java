package org.springframework.samples.petclinic.authentication.web;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.authentication.domain.LoginDTO;
import org.springframework.samples.petclinic.authentication.domain.ResponseLoginDTO;
import org.springframework.samples.petclinic.authentication.util.SecurityUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    public AuthenticationController(AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDTO> login(@RequestBody LoginDTO loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String access_token = this.securityUtil.createToken(authentication);
        ResponseLoginDTO res = new ResponseLoginDTO();
        res.setAccess_token(access_token);
        return ResponseEntity.ok().body(res);
    }
}
