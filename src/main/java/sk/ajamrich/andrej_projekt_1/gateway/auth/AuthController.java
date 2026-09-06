package sk.ajamrich.andrej_projekt_1.gateway.auth;

import sk.ajamrich.andrej_projekt_1.gateway.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;

@RestController
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final InviteCodeService inviteCodeService;
    private final JwtService jwtService;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    public AuthController(
            AuthenticationService authenticationService,
            UserService userService,
            InviteCodeService inviteCodeService,
            JwtService jwtService
    ) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.inviteCodeService = inviteCodeService;
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/login")
    public Mono<ResponseEntity<Object>> login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.authenticate(request.username(), request.password())
                .map(roles -> {
                    String token = jwtService.generateToken(request.username(), roles);
                    return ResponseEntity.ok((Object) new LoginResponse(token, "Bearer", expirationMs));
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body((Object) new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials !")));

    }

    @PostMapping("/auth/users")
    public Mono<ResponseEntity<Object>> createUser(
            @RequestHeader("X-Invite-Code") String inviteCode,
            @Valid @RequestBody CreateUserRequest request) {

        return inviteCodeService.consume(inviteCode)
                .then(userService.createUser(request.username(), request.password(), request.roles()))
                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
                        .body((Object) new CreateUserResponse(
                                user.getUsername(),
                                Arrays.asList(user.getRoles().split(",")))))
                .onErrorResume(InvalidInviteCodeException.class, e -> Mono.just(
                        ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body((Object) new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage()))))
                .onErrorResume(DataIntegrityViolationException.class, e -> Mono.just(
                        ResponseEntity.status(HttpStatus.CONFLICT)
                                .body((Object) new ErrorResponse(HttpStatus.CONFLICT.value(), "User already exist !"))));
    }
}