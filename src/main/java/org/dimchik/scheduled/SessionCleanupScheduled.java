package org.dimchik.scheduled;

import lombok.RequiredArgsConstructor;
import org.dimchik.service.base.SecurityServiceBase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCleanupScheduled {
    private final SecurityServiceBase securityServiceBase;

    @Scheduled(fixedDelayString = "${session.cleanup-interval}")
    public void cleanupSessions() {
        securityServiceBase.cleanupExpiredSessions();
    }
}
