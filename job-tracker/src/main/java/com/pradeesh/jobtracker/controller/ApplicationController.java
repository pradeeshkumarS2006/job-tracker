package com.pradeesh.jobtracker.controller;

import com.pradeesh.jobtracker.entity.Application;
import com.pradeesh.jobtracker.entity.ApplicationStatus;
import com.pradeesh.jobtracker.repository.ApplicationRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationRepository repository;

    public ApplicationController(ApplicationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Application> create(@Valid @RequestBody Application application) {
        Application saved = repository.save(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Application>> getAll(
            @RequestParam(required = false) ApplicationStatus status) {

        List<Application> results = (status != null)
                ? repository.findByStatus(status)
                : repository.findAll();

        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> getOne(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Application> update(@PathVariable Long id, @Valid @RequestBody Application updated) {
        return repository.findById(id).map(existing -> {
            existing.setCompanyName(updated.getCompanyName());
            existing.setRoleTitle(updated.getRoleTitle());
            existing.setDateApplied(updated.getDateApplied());
            existing.setResumeVersion(updated.getResumeVersion());
            existing.setNotes(updated.getNotes());
            existing.setJobDescription(updated.getJobDescription());
            if (updated.getStatus() != null) {
                existing.setStatus(updated.getStatus()); // triggers lastStatusChange reset
            }
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Application> updateStatus(@PathVariable Long id, @RequestParam ApplicationStatus status) {
        return repository.findById(id).map(existing -> {
            existing.setStatus(status);
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
