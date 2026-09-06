package sk.ajamrich.andrej_projekt_1.gateway.auth;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class InviteCodeService {

    private final InviteCodeRepository inviteCodeRepository;

    public InviteCodeService(InviteCodeRepository inviteCodeRepository) {
        this.inviteCodeRepository = inviteCodeRepository;
    }

    /**
     * Overi kod a oznaci ho ako pouzity. Ak je neplatny alebo uz pouzity,
     * vrati Mono s chybou InvalidInviteCodeException.
     */
    public Mono<Void> consume(String code) {
        return inviteCodeRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new InvalidInviteCodeException("Neplatny registracny kod")))
                .flatMap(invite -> {
                    if (Boolean.TRUE.equals(invite.getUsed())) {
                        return Mono.error(new InvalidInviteCodeException("Tento kod uz bol pouzity"));
                    }
                    invite.setUsed(true);
                    invite.setUsedAt(LocalDateTime.now());
                    return inviteCodeRepository.save(invite).then();
                });
    }
}