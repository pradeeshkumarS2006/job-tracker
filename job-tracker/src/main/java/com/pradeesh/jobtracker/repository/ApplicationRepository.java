package com.pradeesh.jobtracker.repository;

import com.pradeesh.jobtracker.entity.Application;
import com.pradeesh.jobtracker.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStatus(ApplicationStatus status);

    List<Application> findByStatusNotInAndLastStatusChangeBefore(
            List<ApplicationStatus> excludedStatuses, LocalDateTime cutoff);
}
