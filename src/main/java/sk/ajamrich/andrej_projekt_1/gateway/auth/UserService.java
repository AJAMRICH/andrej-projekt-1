package sk.ajamrich.andrej_projekt_1.gateway.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserEntity> createUser(String username, String rawPassword, List<String> roles) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setRoles((roles == null || roles.isEmpty()) ? "ROLE_USER" : String.join(",", roles));
        return userRepository.save(user);
    }
}