package sk.ajamrich.andrej_projekt_1.gateway.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Vrati zoznam roli, ak su credentials spravne.
     * Ak su nespravne (zly username alebo heslo), Mono je prazdne.
     */
    public Mono<List<String>> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> encoder.matches(rawPassword, user.getPasswordHash()))
                .map(user -> Arrays.asList(user.getRoles().split(",")));
    }
}