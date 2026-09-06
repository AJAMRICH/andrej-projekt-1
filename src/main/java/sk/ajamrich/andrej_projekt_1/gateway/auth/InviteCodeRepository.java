package sk.ajamrich.andrej_projekt_1.gateway.auth;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InviteCodeRepository extends ReactiveCrudRepository<InviteCodeEntity, Long> {
    Mono<InviteCodeEntity> findByCode(String code);
}