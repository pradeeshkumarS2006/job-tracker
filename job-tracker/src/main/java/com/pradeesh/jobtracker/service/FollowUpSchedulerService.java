package com.pradeesh.jobtracker.service;

import com.pradeesh.jobtracker.entity.Application;
import com.pradeesh.jobtracker.entity.ApplicationStatus;
import com.pradeesh.jobtracker.repository.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowUpSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(FollowUpSchedulerService.class);

    private final ApplicationRepository repository;

    // statuses that are "final" and don't need follow-up nagging
    private static final List<ApplicationStatus> TERMINAL_STATUSES = List.of(
            ApplicationStatus.OFFER, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN
    );

    public FollowUpSchedulerService(ApplicationRepository repository) {
        this.repository = repository;
    }

    /**
     * Runs every day at 8:00 AM.
     * Flags any non-terminal application that hasn't had a status change in 7+ days.
     * Cron format: sec min hour day month weekday
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void flagStaleApplications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        List<Application> stale = repository.findByStatusNotInAndLastStatusChangeBefore(TERMINAL_STATUSES, cutoff);

        for (Application app : stale) {
            app.setFollowUpFlagged(true);
            repository.save(app);
            log.info("FOLLOW-UP NEEDED: {} - {} (no update since {})",
                    app.getCompanyName(), app.getRoleTitle(), app.getLastStatusChange());
        }

        log.info("Follow-up scan complete. {} application(s) flagged.", stale.size());
    }
}
